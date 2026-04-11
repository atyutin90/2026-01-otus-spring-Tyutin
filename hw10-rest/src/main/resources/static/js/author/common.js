export const API_URL = '/api/authors';

export async function show() {
    const authorId = getAuthorId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("author-form"),
        fullName: document.getElementById("full-name"),
        editBtn: document.getElementById("edit-btn"),
        saveDtn: document.getElementById("save-btn"),
    };

    try {
        const author = await apiRequest(`${API_URL}/${authorId}`);
        renderShow(author, ui);
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

export async function edit() {
    const authorId = getAuthorId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("author-form"),
        fullName: document.getElementById("full-name"),
        errorFullName: document.getElementById("error-full-name"),
        id: document.getElementById("id"),
        editBtn: document.getElementById("edit-btn"),
        saveDtn: document.getElementById("save-btn"),
    };

    try {
        if (authorId) {
            const author = await apiRequest(`${API_URL}/${authorId}`);
            renderEdit(author, ui);
        } else {
            ui.form.style.display = "block";
            ui.fullName.disabled = false;
            ui.editBtn.style.display = "none";
            ui.saveDtn.style.display = "block";
        }
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

export async function save() {
    const authorId = getAuthorId();

    const ui = {
        fullName: document.getElementById("full-name"),
        errorFullName: document.getElementById("error-full-name")
    };
    const author = {"id": authorId, "fullName": ui.fullName.value};

    if (authorId) {
        await fetch(`${API_URL}/${authorId}`, {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(author)
        }).then(response => process(response, ui))
    } else {
        await fetch(`${API_URL}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(author)
        }).then(response => process(response, ui))
    }
}

async function process(response, ui) {
    const data = await response.json()
    if (response.ok) {
        window.location.href = `/authors/${data.id}`;
    } else {
        ui.errorFullName.textContent = data.fullName;
    }
}

export function getAuthorId() {
    return document.getElementById("authorId").dataset.id;
}

export async function apiRequest(url) {
    const response = await fetch(url, {
        headers: {'Content-Type': 'application/json'}
    });

    let data = await response.json();

    if (!response.ok) {
        throw new Error(data?.message || `HTTP ${response.status}`);
    }
    return data;
}

function renderShow(author, ui) {
    ui.fullName.value = author.fullName;
    ui.editBtn.style.display = "block";
    ui.editBtn.href = `/authors/${author.id}/edit`;
    ui.form.style.display = "block";
}

function renderEdit(author, ui) {
    ui.id = author.id;
    ui.fullName.disabled = false;
    ui.fullName.value = author.fullName;
    ui.form.style.display = "block";
    ui.editBtn.style.display = "none";
    ui.saveDtn.style.display = "block";
}

function showError(message, ui) {
    ui.error.textContent = message || "Something went wrong";
    ui.error.style.display = "block";
}
