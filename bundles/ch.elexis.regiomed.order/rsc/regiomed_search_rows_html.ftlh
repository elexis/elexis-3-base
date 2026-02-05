<#if availableElexisStocks?has_content>
    <tr id="meta-stocks" style="display:none;" data-stocks="${availableElexisStocks?join(',')}" data-last-filter="${lastFilter!'ALL'}"></tr>
</#if>
<#if products?has_content>
    <#list products as p>
        <tr id='res_row_${p.index}' class="search-row" style="cursor:pointer; ${p.rowStyle}" data-local-stocks='${p.localStocksJson}' onclick='selectSearchResult(${p.index})' ondblclick='applySearchResult(${p.index})' data-has-stock="${hasStockColumn?c}">
            <td>
                <div style="font-weight:bold;">${p.prodName}</div>
                <#if p.manufacturer?has_content>
                    <div class="manufacturer-text">${p.manufacturer}</div>
                </#if>
            </td>
            <td>${p.ean}</td>
            <td class='status-cell'>${p.message}</td>
            <td>
                <#if (p.totalLocalStock> 0)>
                    <div class="local-stock-container" style="display:flex; flex-direction:column; gap:4px;">
                        <#list p.localStocks as name, qty>
                            <#if (qty> 0)>
                                <span class="stock-badge-base" style="${p.getStockBadgeStyle(name)}">
                                    ${name}: ${qty}
                                </span>
                            </#if>
                        </#list>
                    </div>
                    <#else>
                        <span style="color:#ccc; font-size:11px;">-</span>
                </#if>
            </td>
            <#if hasStockColumn>
                <td style="text-align:center;">${p.stock}</td>
            </#if>
            <td class='price-cell' style="text-align:right;">${p.price}</td>
        </tr>
    </#list>
    <#else>
        <tr>
            <td colspan='6' style='text-align:center; padding:20px; color:#888;'>
                ${noResultsMsg}
            </td>
        </tr>
</#if>