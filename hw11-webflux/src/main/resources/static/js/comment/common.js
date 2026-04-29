import {openModal} from "../fragments/delete-modal.js";

export const API_URL = '/api/comments';

export async function init() {
    const commentId = getCommentId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("comment-form"),
        text: document.getElementById("text"),
        deleteBtn: document.getElementById("deleteBtn")
    };
    try {
        if (commentId) {
            const comment = await apiRequest(`${API_URL}/${commentId}`);
            renderEdit(comment, ui);
        } else {
            ui.form.style.display = "block";
        }
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

export async function deleteComment(id) {
    const bookId = getBookId();
    await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(() => window.location.href = `/books/${bookId}`)
}

export async function save() {
    const commentId = getCommentId();
    const bookId = getBookId();
    const ui = {
        text: document.getElementById("text"),
        errorText: document.getElementById("error-text")
    };

    const comment = {"id": commentId, "bookId": bookId, "text": ui.text.value};

    if (commentId) {
        await fetch(`${API_URL}/${commentId}`, {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(comment)
        }).then(response => process(response, ui))
    } else {
        await fetch(`/api/books/${bookId}/comments`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(comment)
        }).then(response => process(response, ui))
    }
}

export async function confirmDelete() {
    const commentId = getCommentId();
    const text = document.getElementById("text").value;
    openModal({
        text: `${text}?`,
        onConfirm: async () => await deleteComment(commentId)
    });
}

async function apiRequest(url) {
    const response = await fetch(url, {
        headers: {'Content-Type': 'application/json'}
    });

    let data = await response.json();

    if (!response.ok) {
        throw new Error(data?.message || `HTTP ${response.status}`);
    }
    return data;
}

async function process(response, ui) {
    const data = await response.json()
    if (response.ok) {
        window.location.href = `/books/${data.bookId}`;
    } else {
        ui.errorText.textContent = data.text;
    }
}

function renderEdit(comment, ui) {
    ui.id = comment.id;
    ui.text.value = comment.text;
    ui.deleteBtn.style.display = "block";
    ui.form.style.display = "block";
}

function showError(message, ui) {
    ui.error.textContent = message || "Something went wrong";
    ui.error.style.display = "block";
}

export function getCommentId() {
    return document.getElementById("commentId").dataset.id;
}

export function getBookId() {
    return document.getElementById("bookId").dataset.id;
}
