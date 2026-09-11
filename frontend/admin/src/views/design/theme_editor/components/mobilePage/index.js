const files = import.meta.glob('./*.vue', { eager: true });

const modules = {};
Object.keys(files).forEach((key) => {
  const name = key.replace(/^\.\/(.*)\.vue$/, '$1');
  modules[name] = files[key].default || files[key];
});

export default modules;
