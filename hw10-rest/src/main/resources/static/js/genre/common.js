export const API_URL = '/api/genres';

export async function show() {
    const genreId = getGenreId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("genre-form"),
        name: document.getElementById("name"),
        editBtn: document.getElementById("edit-btn"),
        saveDtn: document.getElementById("save-btn"),
    };

    try {
        const genre = await apiRequest(`${API_URL}/${genreId}`);
        renderShow(genre, ui);
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

export async function edit() {
    const genreId = getGenreId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("genre-form"),
        name: document.getElementById("name"),
        errorName: document.getElementById("error-name"),
        id: document.getElementById("id"),
        editBtn: document.getElementById("edit-btn"),
        saveDtn: document.getElementById("save-btn"),
    };

    try {
        if (genreId) {
            const genre = await apiRequest(`${API_URL}/${genreId}`);
            renderEdit(genre, ui);
        } else {
            ui.form.style.display = "block";
            ui.name.disabled = false;
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
    const genreId = getGenreId();

    const ui = {
        name: document.getElementById("name"),
        errorName: document.getElementById("error-name")
    };
    const genre = {"id": genreId, "name": ui.name.value};

    if (genreId) {
        await fetch(`${API_URL}/${genreId}`, {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(genre)
        }).then(response => process(response, ui))
    } else {
        await fetch(`${API_URL}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(genre)
        }).then(response => process(response, ui))
    }
}

export function getGenreId() {
    return document.getElementById("genreId").dataset.id;
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

async function process(response, ui) {
    const data = await response.json()
    if (response.ok) {
        window.location.href = `/genres/${data.id}`;
    } else {
        ui.errorName.textContent = data.name;
    }
}

function renderShow(genre, ui) {
    ui.name.value = genre.name;
    ui.editBtn.href = `/genres/${genre.id}/edit`;
    ui.editBtn.style.display = "block";
    ui.form.style.display = "block";
}

function renderEdit(genre, ui) {
    ui.id = genre.id;
    ui.name.value = genre.name;
    ui.name.disabled = false;
    ui.form.style.display = "block";
    ui.editBtn.style.display = "none";
    ui.saveDtn.style.display = "block";
}

function showError(message, ui) {
    ui.error.textContent = message || "Something went wrong";
    ui.error.style.display = "block";
}
