(() => {
 const form=document.querySelector('[data-discount-form]');
 if(form) {
   const percent=form.querySelector('#discountType'),visibility=form.querySelector('#isPublic'),customer=form.querySelector('#customerId');
   function sync() {
     if(percent) { const yes=percent.value==='PERCENT';form.querySelector('[data-percent-only]').hidden=!yes;const max=form.querySelector('#maxDiscount');max.disabled=!yes;const value=form.querySelector('#discountValue');value.min=yes?'1':'0.01';if(yes)value.max='100';else value.removeAttribute('max'); }
     if(visibility) { const privateVoucher=visibility.value==='false';form.querySelector('[data-private-only]').hidden=!privateVoucher;customer.required=privateVoucher;customer.disabled=!privateVoucher; }
   }
   percent?.addEventListener('change',sync);visibility?.addEventListener('change',sync);sync();
 }
 const search=document.querySelector('[data-product-search]');
 if(search) {
   const rows=[...document.querySelectorAll('[data-product-row]')],clear=document.querySelector('[data-clear-product-search]');
   function filter() { const q=search.value.trim().toLocaleLowerCase('vi');let visible=0;rows.forEach(row=>{row.hidden=!row.dataset.search.toLocaleLowerCase('vi').includes(q);if(!row.hidden)visible++;});clear.hidden=!search.value;document.querySelector('[data-products-empty]').hidden=visible>0; }
   function count() { document.querySelector('[data-selection-count]').textContent='Đã chọn '+document.querySelectorAll('[name=productIds]:checked').length+' sản phẩm'; }
   search.addEventListener('input',filter);clear.addEventListener('click',()=>{search.value='';filter();search.focus();});search.form.addEventListener('change',count);filter();count();
 }
})();
