<#if products?has_content>
    <#list products as p>
    <tr id='res_row_${p.index}' 
        onclick='selectSearchResult(${p.index})' 
        ondblclick='applySearchResult(${p.index})'
        style="cursor:pointer;"
        data-has-stock="${hasStockColumn?c}">
        
        <td>
            ${p.prodName}
            <#if p.manufacturer?has_content>
                <span class="manufacturer-text">${p.manufacturer}</span>
            </#if>
        </td>
        <td>${p.ean}</td>
        <td class='status-cell'>${p.message}</td>
        
        <#if hasStockColumn>
            <td style="text-align:center;">${p.stock}</td>
        </#if>
        
        <td class='price-cell' style="text-align:right;">${p.price}</td>
    </tr>
    </#list>
<#else>
    <tr>
        <td colspan='5' style='text-align:center; padding:20px; color:#888;'>
            ${noResultsMsg}
        </td>
    </tr>
</#if>