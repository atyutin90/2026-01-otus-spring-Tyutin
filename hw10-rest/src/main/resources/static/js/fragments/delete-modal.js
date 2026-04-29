let modalInstance = null;
let confirmCallback = null;

export function openModal({ text, onConfirm }) {

    const modalEl = document.getElementById("deleteModal");
    const textBlock = document.getElementById("modalText");

    if (!modalInstance) {
        modalInstance = new bootstrap.Modal(modalEl);
    }

    // modal text
    textBlock.innerText = text;

    // callback
    confirmCallback = onConfirm;

    modalInstance.show();
}

document.getElementById("modalConfirmBtn").addEventListener(
    "click",
    async () => {
        if (confirmCallback) await confirmCallback();
        modalInstance.hide();
        document.activeElement.blur()
});

document.getElementById("modalCancelBtn").addEventListener(
    "click",
    async () => { document.activeElement.blur() });
