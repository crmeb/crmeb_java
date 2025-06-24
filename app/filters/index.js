/**
 * 主题色色值
 */
export function filterTheme(status) {
	const statusMap = {
		'theme1': '#e93323',
		'theme2': '#fe5c2d',
		'theme3': '#42ca4d',
		'theme4': '#1ca5e9',
		'theme5': '#ff448f',
	}
	return statusMap[status]
}