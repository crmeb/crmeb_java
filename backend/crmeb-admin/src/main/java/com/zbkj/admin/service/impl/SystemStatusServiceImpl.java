package com.zbkj.admin.service.impl;



import cn.hutool.core.date.DateUtil;
import com.zbkj.admin.service.SystemStatusService;
import com.zbkj.common.response.SystemStatusResponse;

import com.zbkj.common.utils.CrmebUtil;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import com.zbkj.common.model.systemStatus.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class SystemStatusServiceImpl implements SystemStatusService {

    private static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private static final long startTime = runtimeMXBean.getStartTime();

    @Override
    public SystemStatusResponse getInfo() {
        SystemStatusResponse systemStatusResponse = new SystemStatusResponse();
        //获取CPU信息
        systemStatusResponse.setCpuInfo(getCpuInfo());
        //获取系统内存信息
        systemStatusResponse.setSystemMemoryInfo(getMemoryInfo());
        //获取JVM内存信息
        systemStatusResponse.setJvmMemoryInfo(getJvmMemoryInfo());
        //获取服务器信息
        systemStatusResponse.setServerInfo(getServerInfo());
        //获取JVM信息
        systemStatusResponse.setJvmInfo(getJvmInfo());
        //获取磁盘信息
        systemStatusResponse.setDiskInfos(getDiskInfos());
        return systemStatusResponse;
    }


    public static CpuInfo getCpuInfo() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();

        // 1. 获取 CPU 核心数
        int coreCount = processor.getLogicalProcessorCount();

        // 2. 获取两次 CPU 时间戳（间隔 1 秒）
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        long[] currTicks = processor.getSystemCpuLoadTicks();

        // 3. 计算各时间类型的差值
        long user = currTicks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long system = currTicks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = currTicks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long total = user + system + idle; // 可扩展其他类型（如 IOWAIT）

        // 4. 计算百分比（注意避免除以零）
        double totalTime = total > 0 ? total : 1; // 防止分母为零
        double userUtilization = CrmebUtil.round(user / totalTime);
        double systemUtilization = CrmebUtil.round( system / totalTime);
        double idleRate = CrmebUtil.round(idle / totalTime);

        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.setCoreCount(coreCount);
        cpuInfo.setUserUsageRate(userUtilization);
        cpuInfo.setSystemUsageRate(systemUtilization);
        cpuInfo.setFreeUsageRate(idleRate);
        return cpuInfo;
    }

    public static systemMemoryInfo getMemoryInfo(){
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long totalBytes = memory.getTotal();
        long availableBytes = memory.getAvailable();
        double usageRatio = (totalBytes > 0)  ?
                1.0 - (double) availableBytes / totalBytes : 0.0;
        // 格式化易读单位
        String totalMemory = CrmebUtil.formatBytes(totalBytes);
        String freeMemory = CrmebUtil.formatBytes(availableBytes);
        String usedMemory = CrmebUtil.formatBytes(totalBytes - availableBytes);
        double systemUsageRate = CrmebUtil.round(usageRatio);

        // 封装为Map返回
        systemMemoryInfo systemMemoryInfo = new systemMemoryInfo();
        systemMemoryInfo.setSystemTotal(totalMemory);
        systemMemoryInfo.setSystemUsed(usedMemory);
        systemMemoryInfo.setSystemFree(freeMemory);
        systemMemoryInfo.setSystemUsageRate(systemUsageRate);
        return systemMemoryInfo;
    }

    private static JvmMemoryInfo getJvmMemoryInfo() {
        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        // 堆内存
        MemoryUsage heapUsage = memoryMxBean.getHeapMemoryUsage();

        // 堆内存指标
        long heapMaxBytes = heapUsage.getMax();
        long heapCommittedBytes = heapUsage.getCommitted();
        long heapUsedBytes = heapUsage.getUsed();
        double heapUsageRatio = (heapCommittedBytes > 0) ?
                (double) heapUsedBytes / heapCommittedBytes : 0.0;

        // 格式化字符串
        String heapTotal = CrmebUtil.formatBytes(heapCommittedBytes);
        String heapUsed = CrmebUtil.formatBytes(heapUsedBytes);
        String heapFree = CrmebUtil.formatBytes(heapCommittedBytes - heapUsedBytes);
        double jvmUsageRate = CrmebUtil.round(heapUsageRatio);
        JvmMemoryInfo jvmMemoryInfo = new JvmMemoryInfo();
        jvmMemoryInfo.setJvmTotal(heapTotal);
        jvmMemoryInfo.setJvmUsed(heapUsed);
        jvmMemoryInfo.setJvmFree(heapFree);
        jvmMemoryInfo.setJvmUsageRate(jvmUsageRate);
        return jvmMemoryInfo;
    }

    private static ServerInfo getServerInfo(){
        // 1. 获取服务器名称
        String serverName = null;
        try {
            serverName = new SystemInfo().getOperatingSystem().getNetworkParams().getHostName();
        } catch (Exception e) {
            try {
                serverName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                serverName = "Unknown";
            }
        }
        // 2. 获取主 IPv4 地址（非回环、非虚拟）
        String serverIP = CrmebUtil.getPrimaryIpAddress();

        // 3. 获取操作系统信息（OSHI 提供更详细信息）
        OperatingSystem osInfo = new SystemInfo().getOperatingSystem();
        String os = String.format("%s %s",
                osInfo.getFamily(),
                osInfo.getVersionInfo().getVersion()
        );

        // 4. 系统架构
        String arch = System.getProperty("os.arch");

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerName(serverName);
        serverInfo.setServerIP(serverIP);
        serverInfo.setOs(os);
        serverInfo.setSystemArch(arch);
        return serverInfo;
    }

    private static JvmInfo getJvmInfo(){
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

        // 1. JVM名称 & Java版本
        String jvmName = System.getProperty("java.runtime.name");
        String javaVersion = System.getProperty("java.version");

        // 2. 启动时间 & 运行时长
        Date jvmStartTime = new Date(startTime);

        long uptimeMillis = System.currentTimeMillis() - startTime;
        String uptime = CrmebUtil.formatUptime(uptimeMillis);

        // 3. 安装路径 & 项目路径
        String installPath = System.getProperty("java.home");
        String projectPath = System.getProperty("user.dir");

        // 4. JVM参数（排除默认参数）
        List<String> jvmOptions = runtimeMxBean.getInputArguments()
                .stream()
                .filter(arg -> arg.startsWith("-X") || arg.startsWith("-D"))
                .collect(Collectors.toList());

        JvmInfo jvmInfo = new JvmInfo();
        jvmInfo.setJavaName(jvmName);
        jvmInfo.setJavaVersion(javaVersion);
        jvmInfo.setStartTime(jvmStartTime);
        jvmInfo.setUptime(uptime);
        jvmInfo.setInstallPath(installPath);
        jvmInfo.setProjectPath(projectPath);
        jvmInfo.setJvmOptions(jvmOptions);
        return jvmInfo;
    }

    private static List<DiskInfo> getDiskInfos(){
        List<DiskInfo> diskList = new ArrayList<>();
        try {
            SystemInfo systemInfo = new SystemInfo();
            // 判断是否为Linux系统，以便区分分区名称和类型
            String osName = systemInfo.getOperatingSystem().getFamily();
            boolean isLinux = osName.toLowerCase().contains("linux");
            // 获取所有磁盘分区信息
            List<OSFileStore> fileStores = systemInfo.getOperatingSystem()
                    .getFileSystem()
                    .getFileStores();
            fileStores.stream()
                    .filter(fs ->
                            !fs.getType().equalsIgnoreCase("tmpfs") &&  // 排除临时文件系统
                            !fs.getType().equalsIgnoreCase("ramfs") &&  // 排除内存盘
                            !fs.getType().equalsIgnoreCase("rootfs") && // 排除rootfs
                            !fs.getMount().startsWith("/snap")                      // 可选：排除Snap挂载
                    )
                    .forEach(fs -> {
                        long totalBytes = fs.getTotalSpace();
                        long freeBytes = fs.getFreeSpace();
                        double usedPercentageRate = (totalBytes > 0) ?
                                (1.0 - (double) freeBytes / totalBytes) : 0.0;

                        // 封装为DiskInfo对象并添加到列表中
                        DiskInfo diskInfo = new DiskInfo();
                        diskInfo.setPath(fs.getMount());
                        diskInfo.setFileSystem(fs.getType());
                        diskInfo.setType(isLinux ? "Linux 分区:"+fs.getName() : fs.getName());
                        diskInfo.setTotalSize(CrmebUtil.formatBytes(totalBytes));
                        diskInfo.setFreeSize(CrmebUtil.formatBytes(freeBytes));
                        diskInfo.setUsedSize(CrmebUtil.formatBytes(totalBytes - freeBytes));
                        diskInfo.setUsedPercentage(CrmebUtil.round(usedPercentageRate));
                        diskList.add(diskInfo);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return diskList;
    }

}

