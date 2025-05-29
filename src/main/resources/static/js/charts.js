// Обработка данных с сервера
const processStats = (statsMap) => {
    return Object.entries(statsMap)
        .map(([timestamp, value]) => ({
            timestamp: new Date(timestamp),
            value: value
        }))
        .sort((a, b) => a.timestamp - b.timestamp);
};

// Инициализация данных
let medicineStats = processStats(/*[[${medicineStats}]]*/ {});
let userStats = processStats(/*[[${userStats}]]*/ {});

// Форматирование даты
const formatDate = (date) => {
    return date.toLocaleString('ru-RU', {
        day: 'numeric',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit'
    });
};

// Инициализация графиков
let medicinesChart, usersChart;

const initCharts = () => {
    // График препаратов
    const medicinesCtx = document.getElementById('medicinesChart');
    medicinesChart = new Chart(medicinesCtx, {
        type: 'line',
        data: {
            labels: medicineStats.map(stat => formatDate(stat.timestamp)),
            datasets: [{
                label: 'Количество препаратов',
                data: medicineStats.map(stat => stat.value),
                borderColor: '#4a7df5',
                backgroundColor: 'rgba(74, 125, 245, 0.1)',
                tension: 0.3,
                borderWidth: 2,
                pointRadius: 3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    display: true,
                    title: {display: true, text: 'Время'},
                    ticks: {autoSkip: true, maxTicksLimit: 10}
                },
                y: {
                    beginAtZero: true,
                    title: {display: true, text: 'Количество'},
                    ticks: {stepSize: 1, precision: 0},
                }
            }
        }
    });

    // График пользователей
    const usersCtx = document.getElementById('usersChart');
    usersChart = new Chart(usersCtx, {
        type: 'line',
        data: {
            labels: userStats.map(stat => formatDate(stat.timestamp)),
            datasets: [{
                label: 'Количество пользователей',
                data: userStats.map(stat => stat.value),
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                tension: 0.3,
                borderWidth: 2,
                pointRadius: 3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    display: true,
                    title: {display: true, text: 'Время'},
                    ticks: {autoSkip: true, maxTicksLimit: 10}
                },
                y: {
                    beginAtZero: true,
                    title: {display: true, text: 'Количество'},
                    ticks: {stepSize: 1, precision: 0}
                }
            }
        }
    });
};

// Обновление данных
const updateCharts = async () => {
    try {
        const response = await fetch('/admin/statistics/data');
        const {medicine, users} = await response.json();

        medicineStats = processStats(medicine);
        userStats = processStats(users);

        medicinesChart.data.labels = medicineStats.map(stat => formatDate(stat.timestamp));
        medicinesChart.data.datasets[0].data = medicineStats.map(stat => stat.value);

        usersChart.data.labels = userStats.map(stat => formatDate(stat.timestamp));
        usersChart.data.datasets[0].data = userStats.map(stat => stat.value);

        medicinesChart.update();
        usersChart.update();

    } catch (error) {
        console.error('Ошибка обновления:', error);
    }
};

// Инициализация и управление
document.addEventListener('DOMContentLoaded', () => {
    initCharts();

    const toggle = document.getElementById('autoRefreshToggle');
    let autoRefreshInterval;

    document.getElementById('refreshBtn').addEventListener('click', async function () {
        this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Обновление...';
        await updateCharts();
        this.innerHTML = '<i class="fas fa-sync-alt"></i> Обновить данные';
    });

    toggle.addEventListener('change', () => {
        toggle.checked ? startAutoRefresh() : stopAutoRefresh();
    });

    const startAutoRefresh = () => {
        stopAutoRefresh();
        autoRefreshInterval = setInterval(updateCharts, 30000);
    };

    const stopAutoRefresh = () => {
        clearInterval(autoRefreshInterval);
    };

    if (toggle.checked) startAutoRefresh();
});