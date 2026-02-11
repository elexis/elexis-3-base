var currentPharma = '';
var currentEan = '';
var currentSearchRowId = '';
var lastEditedRowId = null;

var searchTypingTimer;
var selectedSearchIndex = -1;
var currentStockFilter = 'ALL';
var currentStockFilter = localStorage.getItem('regiomed_stock_filter') || 'ALL';

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
    var body = document.getElementById('searchResultsBody');
    body.innerHTML = htmlRows;
    var headerStock = document.getElementById('headerStock');
    var firstRow = body.querySelector('tr.search-row'); 
    if (headerStock) {
        if (firstRow && firstRow.getAttribute('data-has-stock') === 'true') {
            headerStock.style.display = 'table-cell';
        } else {
            headerStock.style.display = 'none';
        }
    }

    var metaRow = document.getElementById('meta-stocks');
    if (metaRow) {
        var stocksStr = metaRow.getAttribute('data-stocks');
        var lastFilter = metaRow.getAttribute('data-last-filter');
        if (lastFilter) {
            currentStockFilter = lastFilter;
        }
        if (stocksStr) {
            renderStockFilters(stocksStr.split(','));
            filterRows(currentStockFilter, false);
        }
    } else {
        document.getElementById('stockFilterContainer').style.display = 'none';
    }
}

function renderStockFilters(stocks) {
    var container = document.getElementById('stockFilterContainer');
    if (!stocks || stocks.length === 0) {
        container.style.display = 'none';
        return;
    }

    var allChecked = (currentStockFilter === 'ALL') ? 'checked' : '';
    var html = '<label class="stock-filter-label"><input type="radio" name="sFilter" value="ALL" ' + allChecked + ' onclick="filterRows(\'ALL\', true)"> Alle Lager</label>';
    
    for (var i = 0; i < stocks.length; i++) {
        var s = stocks[i];
        var checked = (currentStockFilter === s) ? 'checked' : '';
        html += '<label class="stock-filter-label"><input type="radio" name="sFilter" value="' + s + '" ' + checked + ' onclick="filterRows(\'' + s + '\', true)"> ' + s + '</label>';
    }
    
    container.innerHTML = html;
    container.style.display = 'flex';
}

function filterRows(stockCode, save) {
    currentStockFilter = stockCode;
    if (save === true) {
         window.location = 'regiomed:saveFilter:' + stockCode;
    }

    var rows = document.querySelectorAll('#searchResultsBody tr.search-row');
    rows.forEach(function(row) {
        if (stockCode === 'ALL') {
            row.style.display = '';
            return;
        }
        
        var jsonAttr = row.getAttribute('data-local-stocks');
        var show = false;
        if (jsonAttr) {
            try {
                var data = JSON.parse(jsonAttr);
                if (data[stockCode] && data[stockCode] > 0) {
                    show = true;
                }
            } catch(e) {
                console.error("JSON Error", e);
            }
        }
        
        row.style.display = show ? '' : 'none';
    });
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
        alert("Ungültige Menge"); 
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
    var toast = document.getElementById('toast');
    if(toast) {
        toast.innerText = "Erfolgreich angewendet: " + badgeText;
        toast.className = 'toast show';
        setTimeout(function() {
            toast.className = toast.className.replace('show', '');
        }, 3000);
    }
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