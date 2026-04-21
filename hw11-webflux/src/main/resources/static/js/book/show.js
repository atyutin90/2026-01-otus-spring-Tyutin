import {getBookId, edit, save, show} from './common.js';

document.addEventListener('DOMContentLoaded', getBookId() ? show : edit);
window.edit = async () => await edit();
window.save = async () => await save();
