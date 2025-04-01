/**
 * JavaScript for responsive tables
 */
document.addEventListener('DOMContentLoaded', function () {
    // Add data-label attributes to table cells based on their column headers
    const tables = document.querySelectorAll('.table-responsive-stack');

    tables.forEach(function (table) {
        const headerCells = table.querySelectorAll('thead th');
        const headerLabels = Array.from(headerCells).map(cell => cell.textContent.trim());

        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(function (row) {
            const cells = row.querySelectorAll('td');
            cells.forEach(function (cell, index) {
                if (index < headerLabels.length && !cell.hasAttribute('data-label')) {
                    cell.setAttribute('data-label', headerLabels[index] + ':');
                }
            });
        });
    });

    // Handle fixed header tables to adjust their height based on container
    const fixedHeaderTables = document.querySelectorAll('.table-fixed-header');

    fixedHeaderTables.forEach(function (container) {
        const setHeight = function () {
            const rect = container.getBoundingClientRect();
            const windowHeight = window.innerHeight;
            const distanceFromBottom = windowHeight - rect.bottom;
            const maxHeight = Math.min(500, windowHeight - rect.top - 20 + distanceFromBottom);

            container.style.maxHeight = maxHeight + 'px';
        };

        setHeight();
        window.addEventListener('resize', setHeight);
    });
}); 