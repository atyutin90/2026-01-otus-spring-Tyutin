import {getGenreId, edit, save, show} from './common.js';

document.addEventListener('DOMContentLoaded', getGenreId() ? show : edit);

window.edit = async () => await edit();
window.save = async () => await save();
