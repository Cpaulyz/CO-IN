let __choose_file_input = null;

const mountInput = input => {
  document.body.appendChild(input);
  __choose_file_input = input;
};

const unmountInput = () => {
  if (__choose_file_input) {
    document.body.removeChild(__choose_file_input);
    __choose_file_input = null;
  }
};

/**
 * singleton input hidden element
 * @returns
 */
export const chooseFile = async () =>
  new Promise((resolve, reject) => {
    unmountInput();
    // create input
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.style.display = 'none';

    // mount
    mountInput(input);

    // append listener
    input.addEventListener('change', e => {
      const file = input.files[0];
      console.log(`select file`, file);

      // return file first
      resolve(file);

      // then unmount input
      unmountInput();
    });

    // invoke selection
    input.click();
  });
