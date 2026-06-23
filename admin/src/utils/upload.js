export const uploadByPieces = ({ file, success, error }) => {
  if (!file) return;
  if (success) {
    success({ url: '' });
  } else if (error) {
    error(new Error('uploadByPieces is not configured'));
  }
};
