(() => {
 const form=document.getElementById('checkoutForm');if(!form)return;
 const payment=form.querySelector('#paymentMethod'),bank=form.querySelector('#bankInfo');const sync=()=>bank.hidden=payment.value!=='BANK_TRANSFER';payment.addEventListener('change',sync);sync();
 form.addEventListener('submit',event=>{
   let invalid=null;form.querySelectorAll('[required]').forEach(field=>{const message=field.value.trim() ? (field.validity.typeMismatch ? 'Giá trị không đúng định dạng.' : '') : 'Vui lòng nhập thông tin này.';const slot=document.getElementById(field.id+'-error');if(slot)slot.textContent=message;field.setAttribute('aria-invalid',message?'true':'false');if(message && !invalid)invalid=field;});
   if(invalid){event.preventDefault();invalid.focus();return;}
   if(form.dataset.submitting){event.preventDefault();return;}form.dataset.submitting='true';form.setAttribute('aria-busy','true');form.querySelector('#btnPlaceOrder').disabled=true;
 });
 window.addEventListener('pageshow',()=>{delete form.dataset.submitting;form.removeAttribute('aria-busy');form.querySelector('#btnPlaceOrder').disabled=false;});
})();
