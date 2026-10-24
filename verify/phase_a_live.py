"""Live acceptance on localhost only. Requires PHASE_A_TEST_DB and SQLCMDPASSWORD.
Run after starting the app with its datasource explicitly pinned to the same test DB.
Captures cookies/HTML only under ignored verify/. Never selects the production database.
"""
import os,sys,subprocess,re,json,html,time
from pathlib import Path
from urllib.parse import urlencode,urlparse
BASE='http://localhost:18081'
DB=os.environ.get('PHASE_A_TEST_DB','shopbangiay_thu')
assert DB in {'shop_verify','shopbangiay_thu'}
OUT=Path('verify'); OUT.mkdir(exist_ok=True)
rows=[]
def sql(query):
    guard="IF DB_NAME() NOT IN (N'shop_verify',N'shopbangiay_thu') THROW 51000,N'Wrong database',1; "
    r=subprocess.run(['sqlcmd','-S','tcp:localhost,1433','-U','sa','-C','-d',DB,'-b','-f','65001','-W','-h','-1','-Q',guard+query],capture_output=True)
    if r.returncode:raise RuntimeError(r.stderr.decode('utf-8','replace'))
    return r.stdout.decode('utf-8','replace').strip()
def get(path,actor='admin',data=None,label=None,csrf=True,form='/admin/pos',expected=None,contains=None):
    jar=OUT/f'phase-a-{actor}.cookies'
    body=OUT/f'phase-a-{actor}-last.html';headers=OUT/f'phase-a-{actor}-headers.txt'
    args=['curl.exe','-sS','--max-time','25','-c',str(jar),'-b',str(jar),'-o',str(body),'-D',str(headers),'-w','%{http_code}']
    if data is not None:
        if csrf:
            _,page,_=get(form,actor,label='csrf source')
            token=re.search(r'name="_csrf"[^>]*value="([^"]+)"',page)
            assert token,'no CSRF token: '+form
            data={**data,'_csrf':html.unescape(token[1])}
        args+=['--data',urlencode(data,doseq=True)]
    r=subprocess.run(args+[BASE+path],capture_output=True)
    assert r.returncode==0,r.stderr.decode()
    status=int(r.stdout);text=body.read_text(encoding='utf-8');head=headers.read_text(encoding='utf-8')
    location=re.search(r'(?im)^Location:\s*(.+)',head);loc=location[1].strip() if location else ''
    entry={'label':label or path,'method':'POST' if data is not None else 'GET','url':path,'status':status,'location':loc}
    if contains:entry['material_visible']=contains in text
    rows.append(entry)
    if label!='csrf source':print(json.dumps(entry,ensure_ascii=False),flush=True)
    (OUT/'phase-a-live-results.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2),encoding='utf-8')
    if expected is not None:assert status==expected,(path,status,text[:200])
    if contains:assert contains in text,path+' missing '+contains
    return status,text,loc
sys.stdout.reconfigure(encoding='utf-8')
print('DATABASE',sql('SELECT DB_NAME();'),flush=True)
get('/login',expected=200)
get('/login',data={'username':'admin','password':os.environ['PHASE_A_ADMIN_PASSWORD']},form='/login',expected=302,label='admin login')
product=int(sql("SET NOCOUNT ON;SELECT id FROM products WHERE code=N'VERIFY-A-20261024';"))
variant=int(sql(f"SET NOCOUNT ON;SELECT MIN(id) FROM product_variants WHERE product_id={product};"))
material=int(sql(f'SET NOCOUNT ON;SELECT chat_lieu_id FROM product_variants WHERE id={variant};'))
name='Vải canvas'
# Main linked user/admin URLs; status is recorded individually.
paths=['/','/products','/products?promotion=true','/category/1',f'/products/{product}',f'/products?materialId={material}','/cart','/login','/register','/policy','/admin','/admin/dashboard','/admin/products-crud','/admin/products-crud/create',f'/admin/products-crud/edit/{product}',f'/admin/products-crud/view/{product}',f'/admin/products-crud/{product}/variants','/admin/variants','/admin/variants/create','/admin/materials','/admin/categories','/admin/sizes','/admin/inventory',f'/admin/inventory/adjust/{variant}','/admin/pos','/admin/orders','/admin/invoices','/admin/customers','/admin/employees','/admin/vouchers','/admin/promotions','/admin/statistics']
for p in paths:get(p,expected=200)
get(f'/admin/products-crud?materialId={material}&keyword=VERIFY-A',expected=200,contains=name,label='admin material column and filter')
get(f'/admin/variants/create?productId={product}',expected=200,label='preselect product')
get(f'/admin/inventory?variantId={variant}',expected=200,contains=name,label='stock and stock history')
# Independent real customer account through registration form.
tag=str(int(time.time()))[-7:];username='phasea'+tag
get('/register','user',data={'username':username,'password':'VerifyA123!','confirmPassword':'VerifyA123!','fullName':'Khách nghiệm thu A','email':username+'@example.test','phone':'09'+tag.zfill(8)},form='/register',expected=302,label='test user registration')
get('/login','user',data={'username':username,'password':'VerifyA123!'},form='/login',expected=302,label='user login')
get('/cart/add','user',data={'variantId':variant,'quantity':1},form=f'/products/{product}',expected=302,label='cart add CSRF')
get('/cart','user',expected=200,contains=name,label='cart material')
get('/checkout','user',expected=200,contains=name,label='checkout material')
_,_,loc=get('/checkout/place','user',data={'receiverName':'Khách nghiệm thu A','phone':'0900000012','email':username+'@example.test','shippingAddress':'Địa chỉ thử trên bản sao','paymentMethod':'COD'},form='/checkout',expected=302,label='online order')
assert '/orders/success/' in loc,loc
order=int(loc.rstrip('/').split('/')[-1]);get(f'/orders/success/{order}','user',expected=200,contains=name,label='success material')
get(f'/orders/{order}','user',expected=200,contains=name,label='user order material')
get(f'/admin/orders/{order}',expected=200,contains=name,label='admin order material')
# POS and held invoice, with CSRF obtained from its real form.
get('/admin/pos/add',data={'variantId':variant,'quantity':1},expected=302,label='POS add')
get('/admin/pos',expected=200,contains=name,label='POS basket material')
get('/admin/pos/holds',data={'paymentMethod':'CASH'},expected=302,label='save held invoice')
_,page,_=get('/admin/pos',expected=200,contains='Chất liệu: '+name,label='held invoice material before opening')
held=re.search(r'action="/admin/pos/holds/(\d+)/open"',page);assert held
get(f'/admin/pos/holds/{held[1]}/open',data={},expected=302,label='open held invoice')
get('/admin/pos/holds/review',data={},expected=302,label='review held invoice')
_,page,_=get('/admin/pos',expected=200,contains=name,label='held invoice opened material')
checkout=re.search(r'name="checkoutToken"[^>]*value="([^"]+)"',page);assert checkout
_,_,loc=get('/admin/pos/checkout',data={'paymentMethod':'CASH','cashReceived':'500000','checkoutToken':html.unescape(checkout[1])},expected=302,label='POS checkout')
assert '/admin/pos/success/' in loc,loc
posorder=int(loc.rstrip('/').split('/')[-1]);invoice=int(sql(f'SET NOCOUNT ON;SELECT id FROM invoices WHERE order_id={posorder};'))
get(f'/admin/invoices/{invoice}',expected=200,contains=name,label='invoice material')
# Hide in-use material; check exact option disappearance, existing rows and snapshots remain visible.
print('BEFORE_HIDE',sql(f'SELECT id,ten,trang_thai FROM chat_lieu WHERE id={material};SELECT COUNT(*) AS using_variants FROM product_variants WHERE chat_lieu_id={material};SELECT ten_chat_lieu FROM order_details WHERE order_id IN ({order},{posorder});'),flush=True)
get(f'/admin/materials/toggle-status/{material}',data={},form='/admin/materials',expected=302,label='hide material in use')
for p in ['/admin/products-crud/create','/admin/variants/create']:
    _,page,_=get(p,expected=200,label='hidden excluded '+p)
    options=re.findall(r'<option\b[^>]*>\s*([^<]*)</option>',page)
    assert name not in options,p
get(f'/admin/products-crud/view/{product}',expected=200,contains=name,label='hidden existing variants visible')
get(f'/orders/{order}','user',expected=200,contains=name,label='hidden old user order snapshot')
get(f'/admin/orders/{order}',expected=200,contains=name,label='hidden old admin order snapshot')
get(f'/admin/invoices/{invoice}',expected=200,contains=name,label='hidden old invoice snapshot')
print('AFTER_HIDE',sql(f'SELECT id,ten,trang_thai FROM chat_lieu WHERE id={material};SELECT ten_chat_lieu FROM order_details WHERE order_id IN ({order},{posorder});'),flush=True)
get(f'/admin/materials/delete/{material}',data={},form='/admin/materials',expected=302,label='delete used material blocked')
assert '1'==sql(f'SET NOCOUNT ON;SELECT COUNT(*) FROM chat_lieu WHERE id={material};')
get(f'/admin/materials/toggle-status/{material}',data={},form='/admin/materials',expected=302,label='restore material visibility')
get('/admin/materials','guest',expected=302,label='security anonymous admin')
get('/admin/products-crud','user',expected=403,label='security user admin')
get('/admin/products-crud/save',data={},csrf=False,expected=403,label='security product POST no CSRF')
get(f'/admin/materials/toggle-status/{material}',data={'_csrf':'invalid'},csrf=False,expected=403,label='security invalid CSRF hide')
get('/admin/variants/save','user',data={},form='/cart',expected=403,label='security user variant POST')
get(f'/orders/{order}','admin',expected=403,label='security order ownership')
# Live forged data is rejected without creating products.
for label,values in [('security forged size',{'initialSizeIds':'-1','initialPrice':'1','initialQuantity':'1'}),('security negative price stock',{'initialSizeIds':'1','initialPrice':'-1','initialQuantity':'-1'})]:
    get('/admin/products-crud/save',data={'code':'BAD-A-'+tag,'name':'Không được lưu','categoryId':'1','status':'false','initialMaterialId':material,**values},form='/admin/products-crud/create',expected=200,label=label)
assert sql("SET NOCOUNT ON;SELECT COUNT(*) FROM products WHERE code LIKE 'BAD-A-%';")=='0'
_,page,_=get('/admin/products-crud?'+urlencode({'keyword':"%' OR 1=1--",'sort':'p.id;DROP TABLE products'}),expected=200,label='security SQL injection')
assert 'Không tìm thấy sản phẩm' in page
meta={'product':product,'variant':variant,'material':material,'onlineOrder':order,'posOrder':posorder,'invoice':invoice,'username':username,'db':DB}
(OUT/'phase-a-fixture.json').write_text(json.dumps(meta,ensure_ascii=False,indent=2),encoding='utf-8')
print('LIVE ACCEPTANCE PASS',json.dumps(meta),flush=True)
