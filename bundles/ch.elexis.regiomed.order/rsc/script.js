var currentPharma = '';
var currentEan = '';
var currentSearchRowId = '';
var lastEditedRowId = null;

var searchTypingTimer;
var selectedSearchIndex = -1;

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        try {
            var err = document.getElementById('errorModal');
            if (err && err.style.display === 'flex') {
                closeErrorModal();
                return;
            }
            var s = document.getElementById('searchModal');
            if (s && s.style.display === 'flex') {
                closeSearchModal();
                return;
            }
            var q = document.getElementById('qtyModal');
            if (q && q.style.display === 'flex') {
                closeQtyModal();
                return;
            }
            window.closeMainDialog();
        } catch (e) {}
    }
});

function openSearchModal(rowId, pharma, ean, currentName) {
    currentSearchRowId = rowId;
    currentPharma = pharma;
    currentEan = ean;
    selectedSearchIndex = -1;

    var input = document.getElementById('searchInput');
    input.value = currentName;
    document.getElementById('searchResultsBody').innerHTML = '';
    document.getElementById('loading').style.display = 'none';
    document.getElementById('btnApply').disabled = true;

    document.getElementById('searchModal').style.display = 'flex';

    input.focus();
    var len = input.value.length;
    input.setSelectionRange(len, len);

    if (len >= 3) {
        triggerSearch();
    }
}

function closeSearchModal() {
    document.getElementById('searchModal').style.display = 'none';
}

function handleSearchInput(e) {
    if (e.key === 'Escape') return;

    clearTimeout(searchTypingTimer);
    if (e.key === 'Enter') {
        triggerSearch();
    } else {
        var val = document.getElementById('searchInput').value;
        if (val.length >= 3) {
            searchTypingTimer = setTimeout(triggerSearch, 500);
        }
    }
}

function triggerSearch() {
    var val = document.getElementById('searchInput').value;
    if (val.length < 3) return;

    document.getElementById('loading').style.display = 'block';
    document.getElementById('searchResultsBody').innerHTML = '';
    selectedSearchIndex = -1;
    document.getElementById('btnApply').disabled = true;

    window.location = 'regiomed:searchQuery:' + encodeURIComponent(val);
}

function fillSearchResults(htmlRows) {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('searchResultsBody').innerHTML = htmlRows;
    var headerStock = document.getElementById('headerStock');
    var body = document.getElementById('searchResultsBody');
    var firstRow = body.querySelector('tr');
    
    if (headerStock) {
        if (firstRow && firstRow.getAttribute('data-has-stock') === 'true') {
            headerStock.style.display = 'table-cell';
        } else {
            headerStock.style.display = 'none';
        }
    }
}

function selectSearchResult(index) {
    selectedSearchIndex = index;
    var rows = document.querySelectorAll('#searchResultsBody tr');
    rows.forEach(function(r) {
        r.classList.remove('selected-row');
    });

    var row = document.getElementById('res_row_' + index);
    if (row) row.classList.add('selected-row');

    document.getElementById('btnApply').disabled = false;
}

function applySearchResult(index) {
    selectSearchResult(index);
    applySelected();
}

function applySelected() {
    if (selectedSearchIndex > -1) {
        window.location = 'regiomed:selectResult:' + selectedSearchIndex + ':' + currentSearchRowId + ':' + currentPharma + ':' + currentEan;
    }
}

function changeQuantity(pharma, ean, currentQty) {
    currentPharma = pharma;
    currentEan = ean;
    var input = document.getElementById('qtyInput');
    input.value = currentQty;
    document.getElementById('qtyModal').style.display = 'flex';
    input.focus();
    input.select();
}

function closeQtyModal() {
    document.getElementById('qtyModal').style.display = 'none';
}

function handleEnterQty(e) {
    if (e.key === 'Enter') submitQty();
}

function submitQty() {
    var val = document.getElementById('qtyInput').value;
    if (val != null && val != "" && !isNaN(val) && val > 0) {
        closeQtyModal();
        window.location = 'regiomed:updateQty:' + currentPharma + ':' + currentEan + ':' + val;
    } else {
        alert("${messages.invalidQtyAlert?js_string}");
    }
}

function removeArticle(rowId, pharma, ean) {
    var row = document.getElementById(rowId);
    if (row) {
        row.style.opacity = '0.3';
        row.style.textDecoration = 'line-through';
        disableButtons(row);
    }
    window.location = 'regiomed:remove:' + pharma + ':' + ean;
}

function replaceArticle(rowId, orgPharma, orgEan) {
    var sel = document.getElementById('sel_' + rowId);
    if (sel) {
        var val = sel.value;
        var row = document.getElementById(rowId);
        lastEditedRowId = rowId;
        if (row) {
            disableButtons(row);
        }
        window.location = 'regiomed:replace:' + orgPharma + ':' + orgEan + ':' + val;
    }
}

function unlockLastRow() {
    if (lastEditedRowId) {
        var row = document.getElementById(lastEditedRowId);
        if (row) {
            var btns = row.querySelectorAll('button');
            btns.forEach(function(btn) { btn.disabled = false; });
            var sels = row.querySelectorAll('select');
            sels.forEach(function(s) { s.disabled = false; });
        }
        lastEditedRowId = null;
    }
}

function forceOrder(pharma, ean) {
    window.location = 'regiomed:force:' + pharma + ':' + ean;
}

function resetArticle(pharma, ean) {
    window.location = 'regiomed:reset:' + pharma + ':' + ean;
}

function disableButtons(row) {
    var btns = row.querySelectorAll('button');
    btns.forEach(function(btn) {
        btn.disabled = true;
    });
    var sels = row.querySelectorAll('select');
    sels.forEach(function(s) {
        s.disabled = true;
    });
}

function updateRowSuccess(rowId, badgeText) {
    closeSearchModal();
    showToast('${messages.successAppliedPrefix?js_string} ' + badgeText);
}

function showErrorModal(title, message) {
    document.getElementById('modalTitle').innerText = title;
    document.getElementById('modalBody').innerText = message;
    document.getElementById('errorModal').style.display = 'flex';
}

function closeErrorModal() {
    document.getElementById('errorModal').style.display = 'none';
}

function showToast(msg) {
    var t = document.getElementById('toast');
    t.innerText = msg;
    t.className = 'toast show';
    setTimeout(function() {
        t.className = t.className.replace('show', '');
    }, 3000);
}