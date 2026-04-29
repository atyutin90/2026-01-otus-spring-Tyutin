export const API_URL = '/api/books';

export async function show() {
    const bookId = getBookId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("book-form"),
        title: document.getElementById("title"),
        author: document.getElementById("author"),
        genres: document.getElementById("genres"),
        commentsBlock: document.getElementById("comments-block"),
        comments: document.getElementById("comments"),
        noComments: document.getElementById("no-comments"),
        defaultAuthorOption: document.getElementById("default-author-option"),
        editBtn: document.getElementById("edit-btn"),
        addCommentEdit: document.getElementById("add-comment-btn")
    };

    try {
        const book = await apiRequest(`${API_URL}/${bookId}`);
        const authors = await apiRequest(`/api/authors`);
        const genres = await apiRequest(`/api/genres`);
        const comments = await apiRequest(`/api/books/${bookId}/comments`);
        renderShow(book, authors, genres, comments, ui);
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

export async function edit() {
    const bookId = getBookId();
    const ui = {
        loader: document.getElementById("loader"),
        error: document.getElementById("error"),
        form: document.getElementById("book-form"),
        title: document.getElementById("title"),
        author: document.getElementById("author"),
        genres: document.getElementById("genres"),
        editBtn: document.getElementById("edit-btn"),
        saveBtn: document.getElementById("save-btn"),
        addCommentBtn: document.getElementById("add-comment-btn"),
        commentsBlock: document.getElementById("comments-block"),
        comments: document.getElementById("comments"),
        noComments: document.getElementById("no-comments"),
        defaultAuthorOption: document.getElementById("default-author-option")
    };

    try {
        const book = bookId ? await apiRequest(`${API_URL}/${bookId}`) : null
        const authors = await apiRequest(`/api/authors`);
        const genres = await apiRequest(`/api/genres`);
        renderEdit(book, authors, genres, ui);
    } catch (e) {
        showError(e.message, ui);
    } finally {
        ui.loader.style.display = "none";
    }
}

export async function save() {

    const bookId = getBookId();

    const ui = {
        title: document.getElementById("title"),
        errorTitle: document.getElementById("error-title"),
        author: document.getElementById("author"),
        errorAuthor: document.getElementById("error-author"),
        genres: document.getElementById("genres"),
        errorGenres: document.getElementById("error-genres"),
    };
    const book = {"id": bookId, title: ui.title.value, "authorId": ui.author.value, "genreIds": Array.from(ui.genres.selectedOptions).map(o => o.value) };

    if (bookId) {
        await fetch(`${API_URL}/${bookId}`, {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(book)
        }).then(response => process(response, ui))
    } else {
        await fetch(`${API_URL}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(book)
        }).then(response => process(response, ui))
    }
}

export function getBookId() {
    return document.getElementById("bookId").dataset.id;
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

function renderShow(book, authors, genres, comments, ui) {

    const authorBlock = getAuthorBlock(ui, book, authors);
    const genresBlock = getGenresBlock(genres, book);

    const commentsBlock = comments.map(comment =>
        `<a href="/books/${book.id}/comments/${comment.id}"
                   class="card p-2 text-decoration-none text-dark">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <i class="bi bi-chat-left-text text-secondary me-1"></i>
                            <span class="text-break" style="white-space: pre-wrap;">${comment.text}</span>
                        </div>
                    </div>
                </a>`
    )
        .join('');

    if (comments.length > 0) {
        ui.noComments.style.display = "none"
    }

    ui.title.value = book.title;
    ui.author.insertAdjacentHTML("beforeend", authorBlock)
    ui.genres.insertAdjacentHTML("beforeend", genresBlock)
    ui.comments.insertAdjacentHTML("beforeend", commentsBlock)
    ui.editBtn.style.display = "block"
    ui.editBtn.href = `/books/${book.id}/edit`;
    ui.addCommentEdit.href = `/books/${book.id}/comments`;
    ui.form.style.display = "block";
    ui.commentsBlock.style.display = "block";
}

function getAuthorBlock(ui, book, authors) {
    return ui.defaultAuthorOption.outerHTML.concat(
        book?.authorId ?
            authors.map(author => `<option value="${author.id}" ${book?.authorId === author.id ? "selected" : ""}>${author.fullName}</option>`) :
            authors.sort((a, b) => a?.fullName.localeCompare(b?.fullName))
                .map(author => `<option value="${author.id}">${author.fullName}</option>`)
    );
}

function getGenresBlock(genres, book) {
    return genres
        .sort((a, b) => a?.name.localeCompare(b?.name))
        .map(genre => `<option value="${genre.id}" ${book?.genreIds?.includes(genre.id) ? "selected" : ""}>${genre.name}</option>`)
        .join('');
}

function renderEdit(book, authors, genres, ui) {
    ui.author.textContent = ui.defaultAuthorOption.textContent
    ui.genres.textContent = ''

    const authorBlock = getAuthorBlock(ui, book, authors)
    const genresBlock = getGenresBlock(genres, book);

    ui.title.value = book?.title || null;
    ui.editBtn.style.display = "none"
    ui.saveBtn.style.display = "block"
    ui.addCommentBtn.style.display = "none"
    ui.title.disabled = false;
    ui.author.disabled = false;
    ui.genres.disabled = false;
    ui.commentsBlock.style.display = "none";

    ui.author.insertAdjacentHTML("beforeend", authorBlock)
    ui.genres.insertAdjacentHTML("beforeend", genresBlock)
    ui.form.style.display = "block";
}

async function process(response, ui) {
    const data = await response.json()
    if (response.ok) {
        window.location.href = `/books/${data.id}`;
    } else {
        ui.errorTitle.textContent = data?.title;
        ui.errorAuthor.textContent = data?.authorId;
        ui.errorGenres.textContent = data?.genreIds;
    }
}

function showError(message, ui) {
    console.error(message)
    ui.error.textContent = message || "Something went wrong";
    ui.error.style.display = "block";
}
