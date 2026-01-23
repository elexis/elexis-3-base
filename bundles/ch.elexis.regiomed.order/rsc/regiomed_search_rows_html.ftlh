<#if products?has_content>
<#list products as p>
<tr id='res_row_${p.index}' 
   onclick='selectSearchResult(${p.index})' 
   ondblclick='applySearchResult(${p.index})'
   data-has-stock="${hasStockColumn?c}">
   <td>${p.prodName}</td>
   <td>${p.ean}</td>
   <td class='status-cell'>${p.message}</td>
   <#if hasStockColumn>
   <td style="text-align:center;">${p.stock}</td>
   </#if>
   <td class='price-cell'>${p.price}</td>
</tr>
</#list>
<#else>
<tr>
   <td colspan='5' style='text-align:center; padding:20px; color:#888;'>
      ${noResultsMsg}
   </td>
</tr>
</#if>
