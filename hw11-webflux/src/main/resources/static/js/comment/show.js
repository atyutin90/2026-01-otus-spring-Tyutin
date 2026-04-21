import {init, save, confirmDelete} from './common.js';

document.addEventListener("DOMContentLoaded", init)

window.save = async () => await save();
window.confirmDelete = async () => await confirmDelete();
