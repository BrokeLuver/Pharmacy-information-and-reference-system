document.addEventListener('DOMContentLoaded', function () {
    const searchButton = document.getElementById('searchButton');
    const searchInput = document.getElementById('searchInput');
    const table = document.querySelector('.user-table');

    if (searchButton && searchInput && table) {
        const performSearch = () => {
            const searchText = searchInput.value.toLowerCase();
            const rows = table.querySelectorAll('tbody tr');

            rows.forEach(row => {
                let match = false;
                // Исключаем последний столбец с кнопками из поиска
                const cells = row.querySelectorAll('td:not(:last-child)');

                cells.forEach(cell => {
                    if (cell.textContent.toLowerCase().includes(searchText)) {
                        match = true;
                    }
                });
                row.style.display = match ? '' : 'none';
            });
        };

        // Обработчики событий
        searchButton.addEventListener('click', performSearch);

        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });

        searchInput.addEventListener('input', (e) => {
            if (e.target.value === '') {
                const rows = table.querySelectorAll('tbody tr');
                rows.forEach(row => row.style.display = '');
            } else {
                performSearch();
            }
        });

        // Первоначальный поиск при загрузке страницы (если есть значение)
        if (searchInput.value) performSearch();
    }
});