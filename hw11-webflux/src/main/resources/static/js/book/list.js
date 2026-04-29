import {openModal} from '../fragments/delete-modal.js';
import {API_URL, apiRequest} from './common.js';

document.addEventListener("DOMContentLoaded", init)

async function init() {

    const ui = {
        loader: document.getElementById("loader"),
        form: document.getElementById("books-container")
    }

    ui.form.replaceChildren();

    try {
        const books = await apiRequest(API_URL);
        renderBooks(books, ui);
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

function renderBooks(books, ui) {
    const booksBlock = books.map(book => `
                 <div class="col-md-4">
                    <div class="card h-100 shadow-sm text-center">
        
                        <div class="card-body">
                            <i class="bi bi-person display-4"></i>
                            <h5 class="mt-3">${book.title}</h5>
                        </div>
        
                        <div class="card-footer bg-white border-0 d-flex justify-content-center gap-2">
        
                            <!-- show -->
                            <a href="/books/${book.id}"
                               class="btn btn-outline-primary btn-sm">
                                <i class="bi bi-eye"></i>
                            </a>
        
                            <!-- delete -->
                            <button 
                                    class="btn btn-outline-danger btn-sm"
                                    onclick="confirmDelete(${book.id}, '${book.title}')">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                    
                 </div>
            `).join('');

    ui.form.insertAdjacentHTML("afterbegin", booksBlock)
}

function showError(message, ui) {
    ui?.error?.textContent?.set(message || "Something went wrong");
}

async function deleteBook(id) {
    await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(init)
}

window.confirmDelete = function (id, name) {
    openModal({
        text: `${name}?`,
        onConfirm: async () => await deleteBook(id)
    });
}
