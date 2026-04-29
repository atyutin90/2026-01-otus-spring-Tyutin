import {openModal} from '../fragments/delete-modal.js';
import {API_URL, apiRequest} from './common.js';

document.addEventListener("DOMContentLoaded", init)

async function init() {

    const ui = {
        loader: document.getElementById("loader"),
        form: document.getElementById("authors-container")
    }

    ui.form.replaceChildren();

    try {
        const authors = await apiRequest(API_URL);
        renderAuthors(authors, ui);
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

async function deleteAuthor(id) {
    await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
        .then(init)
}

function renderAuthors(authors, ui) {
    const authorsBlock = authors.map(author => `
                 <div class="col-md-4">
                    <div class="card h-100 shadow-sm text-center">
        
                        <div class="card-body">
                            <i class="bi bi-person display-4"></i>
                            <h5 class="mt-3">${author.fullName}</h5>
                        </div>
        
                        <div class="card-footer bg-white border-0 d-flex justify-content-center gap-2">
        
                            <!-- show -->
                            <a href="/authors/${author.id}"
                               class="btn btn-outline-primary btn-sm">
                                <i class="bi bi-eye"></i>
                            </a>
        
                            <!-- delete -->
                            <button 
                                    class="btn btn-outline-danger btn-sm"
                                    onclick="confirmDelete(${author.id}, '${author.fullName}')">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                    
                 </div>
            `).join('');

    ui.form.insertAdjacentHTML("afterbegin", authorsBlock)
}

function showError(message, ui) {
    ui.error.textContent = message || "Something went wrong";
}

window.confirmDelete = function (id, name) {
    openModal({
        text: `${name}?`,
        onConfirm: async () => await deleteAuthor(id)
    });
}
