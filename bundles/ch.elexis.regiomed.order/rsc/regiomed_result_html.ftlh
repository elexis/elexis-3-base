<!DOCTYPE html>
<html>
   <head>
      <meta charset='UTF-8'>
      <style>
         ${cssContent?no_esc}
      </style>
      <script>
         <#include "script.js">
      </script>
   </head>
   <body>
      <div id='toast' class='toast'>${messages.successApplied?no_esc}</div>
      <div id="qtyModal" class="modal-overlay">
         <div class="modal-content">
            <div class="modal-header">${messages.changeQtyTitle?no_esc}</div>
            <div>${messages.enterNewQty?no_esc}</div>
            <div style="margin-top:10px;">
               <input type="number" id="qtyInput" class="modal-input" min="1" onkeypress="handleEnterQty(event)">
            </div>
            <div class="modal-footer">
               <button class="btn-modal btn-cancel" onclick="closeQtyModal()">${messages.cancel?no_esc}</button>
               <button class="btn-modal btn-confirm" onclick="submitQty()">${messages.apply?no_esc}</button>
            </div>
         </div>
      </div>
      <div id="errorModal" class="modal-overlay" style="z-index: 3000;">
         <div class="modal-content">
            <div class="modal-header" style="color:#dc3545;">
               <#if imgWarning??>
               <img src='${imgWarning}' style='height:24px;vertical-align:text-bottom;margin-right:8px;'>
               </#if>
               <span id="modalTitle">${messages.errorTitle?no_esc}</span>
            </div>
            <div id="modalBody" class="modal-body"></div>
            <div class="modal-footer">
               <button class="btn-modal btn-cancel" onclick="closeErrorModal()">${messages.understood?no_esc}</button>
            </div>
         </div>
      </div>
      <div id="searchModal" class="modal-overlay" style="z-index: 2000;">
         <div class="modal-content modal-large">
            <div class="modal-header">${messages.searchAltTitle?no_esc}</div>
            <div class="search-bar">
               <input type="text" id="searchInput" class="modal-input" spellcheck="false" placeholder="${messages.searchPlaceholder}" onkeyup="handleSearchInput(event)">
               <button class="btn-modal btn-confirm" onclick="triggerSearch()">${messages.searchBtn?no_esc}</button>
            </div>
            <div id="stockFilterContainer" class="stock-filter-bar" style="display:none;">
            </div>
            <div class="results-container">
               <div id="loading" class="loading">${messages.searching?no_esc}</div>
               <table class="results-table">
                  <thead>
                     <tr>
                        <th>${messages.colName?no_esc}</th>
                        <th style="width:100px;">EAN</th>
                        <th style="width:120px;">${messages.colStatus?no_esc}</th>
                        <th style="width:50px;">${messages.colLager?no_esc}</th>
                        <th id="headerStock" style="width:80px; text-align:center; display:none;">${messages.stockLabel?no_esc}</th>
                        <th style="width:70px; text-align:right;">${messages.colPrice?no_esc}</th>
                     </tr>
                  </thead>
                  <tbody id="searchResultsBody">
                  </tbody>
               </table>
            </div>
            <div class="modal-footer">
               <button id="btnApply" class="btn-modal btn-confirm" onclick="applySelected()" disabled>${messages.apply?no_esc}</button>
               <button class="btn-modal btn-cancel" onclick="closeSearchModal()">${messages.close?no_esc}</button>
            </div>
         </div>
      </div>
      <div class='container'>
         <div class='header'>
            <div class='header-left'>
               <#if logoBase64??>
               <img src='${logoBase64}' class='logo-img' alt='Regiomed Logo'>
               </#if>
               <div class='logo-text'>${messages.orderTitle?no_esc}</div>
            </div>
            <div>${currentDate}</div>
         </div>
         <div class='status-box <#if hasActiveErrors>status-warn<#else>status-ok</#if>'>
            <h3>${messages.checkResult?no_esc}</h3>
            <#if hasActiveErrors>
            <p>${responseMessage?no_esc}</p>
            <#else>
            <p>${messages.allChecksSuccess?no_esc}</p>
            </#if>
         </div>
         <#if nokItems?has_content>
         <#if hasActiveErrors>
         <div class='hint-box'>${messages.hintText?no_esc}</div>
         <div class='section-title warning-section'>${messages.problematicItems?no_esc}</div>
         <#else>
         <div class='section-title corrected-section'>${messages.correctedItems?no_esc}</div>
         </#if>
         <table>
            <thead>
               <tr>
                  <th>${messages.colArticle?no_esc}</th>
                  <th>${messages.colAmount?no_esc}</th>
                  <th>${messages.colInfo?no_esc}</th>
                  <th style="text-align: center;">${messages.colStatus?no_esc}</th>
                  <th>${messages.colAction?no_esc}</th>
               </tr>
            </thead>
            <tbody>
               <#list nokItems as item>
               <tr id='${item.rowId}' class='${item.rowClass}'>
                  <td class='${item.contentClass}'>
                     ${item.description?no_esc}<br>
                     <small style='color:#888'>
                     ${messages.pharmaLabel} ${item.pharmaCode}<br>
                     ${messages.stockLabel} ${item.stock}<br>
                     EAN: ${item.ean}
                     </small>
                  </td>
                  <td class='qty-editable ${item.contentClass}' 
                     title='${messages.clickToEdit}'
                     onclick="changeQuantity('${item.pharmaCode}', '${item.ean}', '${item.quantity}')">
                     ${item.quantity}
                     <#if item.showEditIcon>
                     <#if imgEdit??>
                     <img src='${imgEdit}' style='height:16px; width:16px; vertical-align:text-bottom; opacity:0.6;' alt='Edit'>
                     <#else>
                     <span style='font-size:10px; color:#999;'>✎</span>
                     </#if>
                     </#if>
                  </td>
                  <td class='${item.infoClass}'>
                     ${item.infoText?no_esc}
                     <#if item.hasAlternatives>
                     <div class='alt-container'>
                        <div style='font-weight:bold; margin-top:5px;'>${messages.availableAlternatives?no_esc}</div>
                        <select id='sel_${item.rowId}' class='alt-select' <#if item.disabled>disabled</#if>>
                        <#list item.alternatives as alt>
                        <option value='${alt.value}'>${alt.label?no_esc}</option>
                        </#list>
                        </select>
                     </div>
                     <#elseif item.errorTable && !item.stockError>
                     <div style='margin-top:5px; font-style:italic; font-size:12px; color:#dc3545;'>
                        ${messages.noAlternativeAvailable?no_esc}
                     </div>
                     </#if>
                  </td>
                  <td class='${item.contentClass}'>
                     <div class="status-column">
                        <span class='badge ${item.badgeClass}' id='status_${item.rowId}'>
                        ${item.badgeText?no_esc}
                        </span>
                        <#if item.replacementName?has_content>
                        <div class="replacement-name">
                           (${item.replacementName?no_esc})
                        </div>
                        </#if>
                     </div>
                  </td>
                  <td>
                     <div class='action-btn-container <#if item.btnCount gt 2>layout-column<#else>layout-row</#if>'>
                        <#if item.canReset>
                        <button class='btn-base btn-reset' onclick="resetArticle('${item.pharmaCode}', '${item.ean}')">
                        ${messages.btnReset}
                        </button>
                        <#else>
                        <#if item.showForceBtn>
                        <button class='btn-base' style='border:1px solid #ffc107; color:#856404' 
                           onclick="forceOrder('${item.pharmaCode}', '${item.ean}')">
                        ${messages.btnForce}
                        </button>
                        </#if>
                        <#if isSearchAvailable>
                        <button class='btn-base btn-search' 
                        onclick="openSearchModal('${item.rowId}', '${item.pharmaCode}', '${item.ean}', '${item.escapedDescription?no_esc}')"
                        <#if item.disabled>disabled</#if>>
                        ${messages.btnSearch}
                        </button>
                        </#if>
                        <#if item.hasAlternatives>
                        <button class='btn-base btn-replace' 
                        onclick="replaceArticle('${item.rowId}', '${item.pharmaCode}', '${item.ean}')"
                        <#if item.disabled>disabled</#if>>
                        ${messages.btnReplace}
                        </button>
                        </#if>
                        <button class='btn-base btn-delete' 
                        onclick="removeArticle('${item.rowId}', '${item.pharmaCode}', '${item.ean}')"
                        <#if item.disabled>disabled</#if>>
                        ${messages.btnDelete}
                        </button>
                        </#if>
                     </div>
                  </td>
               </tr>
               </#list>
            </tbody>
         </table>
         </#if>
         <#if okItems?has_content>
         <div class='section-title'>${messages.availableItems?no_esc}</div>
         <table>
            <thead>
               <tr>
                  <th>${messages.colArticle?no_esc}</th>
                  <th>${messages.colAmount?no_esc}</th>
                  <th>${messages.colInfo?no_esc}</th>
                  <th style="text-align: center;">${messages.colStatus?no_esc}</th>
                  <th>${messages.colAction?no_esc}</th>
               </tr>
            </thead>
            <tbody>
               <#list okItems as item>
               <tr id='${item.rowId}' class='${item.rowClass}'>
                  <td class='${item.contentClass}'>
                     ${item.description?no_esc}<br>
                     <small style='color:#888'>
                     ${messages.pharmaLabel} ${item.pharmaCode}<br>
                     ${messages.stockLabel} ${item.stock}<br>
                     EAN: ${item.ean}
                     </small>
                  </td>
                  <td class='qty-editable ${item.contentClass}' 
                     title='${messages.clickToEdit}'
                     onclick="changeQuantity('${item.pharmaCode}', '${item.ean}', '${item.quantity}')">
                     ${item.quantity}
                     <#if item.showEditIcon>
                     <#if imgEdit??>
                     <img src='${imgEdit}' style='height:16px; width:16px; vertical-align:text-bottom; opacity:0.6;' alt='Edit'>
                     <#else>
                     <span style='font-size:10px; color:#999;'>✎</span>
                     </#if>
                     </#if>
                  </td>
                  <td class='${item.contentClass}'>${item.infoText?no_esc}</td>
                  <td class='${item.contentClass}'>
                     <div class="status-column">
                        <span class='badge ${item.badgeClass}' id='status_${item.rowId}'>
                        ${item.badgeText?no_esc}
                        </span>
                        <#if item.replacementName?has_content>
                        <div class="replacement-name">
                           (${item.replacementName?no_esc})
                        </div>
                        </#if>
                     </div>
                  </td>
                  <td>
                     <div class='action-btn-container layout-row'>
                        <#if item.canReset>
                        <button class='btn-base btn-reset' onclick="resetArticle('${item.pharmaCode}', '${item.ean}')">
                        ${messages.btnReset}
                        </button>
                        <#else>
                        <#if isSearchAvailable>
                        <button class='btn-base btn-search' 
                        onclick="openSearchModal('${item.rowId}', '${item.pharmaCode}', '${item.ean}', '${item.escapedDescription?no_esc}')"
                        <#if item.disabled>disabled</#if>>
                        ${messages.btnSearch}
                        </button>
                        </#if>
                        <button class='btn-base btn-delete' onclick="removeArticle('${item.rowId}', '${item.pharmaCode}', '${item.ean}')"
                        <#if item.disabled>disabled</#if>>
                        ${messages.btnDelete}
                        </button>
                        </#if>
                     </div>
                  </td>
               </tr>
               </#list>
            </tbody>
         </table>
         </#if>
      </div>
   </body>
</html>