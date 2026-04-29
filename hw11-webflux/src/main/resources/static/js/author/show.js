import {show, edit, save, getAuthorId} from './common.js';

document.addEventListener('DOMContentLoaded', getAuthorId() ? show : edit);
window.edit = async () => await edit();
window.save = async () => await save();
