<!DOCTYPE html>
<html>
<head>
    <meta charset='UTF-8'>
    <style>${cssContent?no_esc}</style>
    <script>
        <#include "script.js">
    </script>
</head>
<body style="background-color: #f4f6f9; padding: 10px;">
    <div class="container" style="max-width: 100%; min-height: 95vh;">
        <div class="modal-header" style="display: flex; align-items: center;">
            <#if logoBase64??>
                <img src="${logoBase64}" style="height: 45px; margin-right: 15px;" alt="Logo">
            </#if>
            <span>${messages.searchAltTitle?no_esc}</span>
        </div>
        
        <div class="search-bar">
            <input type="text" id="searchInput" class="modal-input" 
                   placeholder="${messages.searchPlaceholder}" 
                   onkeyup="handleSearchInput(event)">
            <button class="btn-modal btn-confirm" onclick="triggerSearch()">
                ${messages.searchBtn?no_esc}
            </button>
        </div>

        <div id="stockFilterContainer" class="stock-filter-bar" style="display:none;"></div>

        <div class="results-container">
            <div id="loading" class="loading" style="display:none;">${messages.searching?no_esc}</div>
            
            <table class="results-table" style="table-layout: fixed; width: 100%;">
                <thead>
                    <tr>
                        <th id="headerImage" style="width: 95px; display:none;"></th>
                        
                        <th style="width: auto;">${messages.colName?no_esc}</th>
                        
                        <th style="width: 110px;">EAN</th>
                        
                        <th style="width: 120px;">${messages.colStatus?no_esc}</th>
                        
                        <th style="width: 90px; text-align: center;">${messages.colLager?no_esc}</th>
                        
                        <th id="headerStock" style="width: 100px; text-align: center; display:none;">
                            ${messages.stockLabel?no_esc}
                        </th>
                        
                        <th style="width: 80px; text-align: right;">${messages.colPrice?no_esc}</th>
                    </tr>
                </thead>
                <tbody id="searchResultsBody"></tbody>
            </table>
        </div>
    </div>
</body>
</html>