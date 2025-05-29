document.addEventListener('DOMContentLoaded', function() {
    const navbar = document.querySelector('.navbar');
    const contentWrappers = document.querySelectorAll('.content-wrapper');
    const navbarCollapse = document.getElementById('navbarNav');

    // Инициализируем CSS-переменную
    document.documentElement.style.setProperty('--navbar-height', `${navbar.offsetHeight}px`);

    function handleTransitionStart() {
        contentWrappers.forEach(wrapper => {
            wrapper.style.transition = 'margin-top 0.25s ease-out';
        });
    }

    function updateContentMargin() {
        const navbarHeight = navbar.offsetHeight;
        // Обновляем CSS-переменную
        document.documentElement.style.setProperty('--navbar-height', `${navbarHeight}px`);

        contentWrappers.forEach(wrapper => {
            wrapper.style.marginTop = `${navbarHeight + 30}px`;
        });
    }

    function handleTransitionEnd() {
        contentWrappers.forEach(wrapper => {
            wrapper.style.transition = '';
        });
    }

    if (navbarCollapse) {
        // Обработчики для Bootstrap collapse
        navbarCollapse.addEventListener('show.bs.collapse', handleTransitionStart);
        navbarCollapse.addEventListener('shown.bs.collapse', () => {
            updateContentMargin();
            handleTransitionEnd();
        });
        navbarCollapse.addEventListener('hide.bs.collapse', handleTransitionStart);
        navbarCollapse.addEventListener('hidden.bs.collapse', () => {
            updateContentMargin();
            handleTransitionEnd();
        });
    }

    // Обновляем при ресайзе
    window.addEventListener('resize', () => {
        handleTransitionStart();
        updateContentMargin();
        setTimeout(handleTransitionEnd, 250);
    });

    // Первоначальная настройка
    updateContentMargin();
});