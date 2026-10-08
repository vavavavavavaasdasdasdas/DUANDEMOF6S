---
version: alpha
name: "ShoeAdmin"
description: "Không gian vận hành cửa hàng giày, nối sản phẩm–size–tồn kho bằng bảng dữ liệu rõ ràng."
colors:
  primary: "#b94312"
  secondary: "#272d2d"
  background: "#f4f5f7"
  surface: "#ffffff"
  border: "#e6e8ec"
  muted: "#59636f"
typography:
  sans:
    fontFamily: "'Inter', system-ui, -apple-system, sans-serif"
  mono:
    fontFamily: "ui-monospace, monospace"
rounded:
  DEFAULT: "0.5rem"
  panel: "0.75rem"
spacing:
  panel: "22px"
  page: "34px"
components:
  button: {}
  table: {}
  input: {}
  dialog: {}
---
# ShoeAdmin Design System

## Overview
Audience: quản trị viên cửa hàng giày Việt Nam; dùng desktop tại quầy, tablet/mobile kiểm tra tồn. Register: ứng dụng quản trị, vi-VN, VND, UTC+7. Không có yêu cầu thị trường Nhật.
Creative North Star: phiếu hàng tại quầy, thông tin mã–size–tồn được đọc nhanh theo hàng.
Signature: giữ dấu S cam và sidebar của Phase 5; mã sản phẩm và lịch sử biến động là điểm nhấn chức năng, không thêm hero hay hiệu ứng trang trí.
Runtime canonical: `static/css/design-system.css` sở hữu các biến --bs-*; `static/css/admin.css` sở hữu layout và trạng thái Admin. Tài liệu này mô tả runtime, không sinh token. Không đổi nhận diện Phase 5.

## Colors
Cam chỉ cho thao tác chính/đang chọn; chữ tối, nền xám nhẹ, panel trắng. Lỗi có chữ và viền, trạng thái có nhãn, không chỉ màu.

## Typography
Stack hiện có hỗ trợ tiếng Việt; tiêu đề 26–29px, bảng/form 13–14px, nhãn phụ 12px. Số tiền/tồn dùng tabular-nums; không cắt tên quan trọng.

## Layout
Sidebar 244px desktop, drawer ở ≤1024px, một cột form ở ≤850px. Page padding desktop 34px, mobile 16px. Form cuộn theo trang. Table cuộn ngang trong panel, sản phẩm phân trang 10/20/50 tại DB; các danh sách variant/lookup/tồn có phân trang phía server. Không khóa chiều cao trang theo bảng.

## Elevation & Depth
Panel viền nhẹ; modal dùng top layer của dialog, không cạnh tranh z-index sidebar. Không hover nhấc hàng/bảng.

## Shapes
Control bo 0.5rem theo biến dùng chung, panel 0.75rem. Ảnh sản phẩm giữ khung vuông cố định.

## Components
### Foundational visual states
Focus vòng cam; disabled giảm opacity và cursor chờ/không cho phép. Busy giữ kích thước nút, đánh dấu aria-busy.
### Buttons and actions
Một thao tác Lưu chính; Quay lại/Hủy là phụ; Xóa tách riêng và xác nhận đối tượng/hậu quả.
### Navigation and data display
Sidebar, topbar, breadcrumb, flash dùng fragment Admin Phase 5. Bảng HTML có nhãn cột, tổng bản ghi, empty/no-results và phân trang.
### Forms and overlays
Native select được chấp nhận với popup do hệ điều hành sở hữu; không yêu cầu popup tùy biến. Shared admin-catalog.js quản lý validation, pending, xác nhận và dirty state. PNG/JPEG ≤5MB; preview có khung cố định. Textarea auto-grow, resize:none.
### Iconography
Dùng chữ cho tất cả thao tác CRUD, không thêm thư viện icon.
### Motion
Giữ motion tối thiểu; prefers-reduced-motion được tôn trọng. Không thêm skeleton.
### Content and data visualization
Tiếng Việt trực tiếp: Lưu sản phẩm, Lưu variant, Điều chỉnh tồn kho. VND định dạng vi-VN. Lịch sử có người thực hiện, lý do và thời gian.
## Do's and Don'ts
- Giữ route và số liệu thật, lỗi ngay cạnh field.
- Dùng token và fragment hiện có.
- Không giả log, không đổi tiền trên đơn lịch sử khi giá variant thay đổi.
- Không dùng alert/confirm/prompt cho thao tác trong ứng dụng.


## Phase 7 profile screens
Customer/Employee reuse catalog tables and forms. Profile details use a two-column definition list, one column on mobile. Account actions live on detail pages; show impact before confirming and protect the current session account. No new design tokens or libraries.

## Phase 8 discount screens
Voucher/Promotion reuse Admin panels, field errors, flash and confirmation. Date/time uses native datetime-local (platform locale, shop UTC+7 semantics). Product selection uses labelled native checkboxes; local name/code search hides rows without dropping selections. No new visual tokens. Checkout/POS share voucher-preview.js for busy, stale-response, error and discount reset states.

## Phase 9 sales screens
POS uses the Admin shell with a product grid and a separate session basket. Desktop has two columns; tablet/mobile stacks below 820px. The product picker is a business variant of the existing native dialog owner; stock-filtered native size select, Escape and cancel restore focus. Product upload/local images are used; legacy external image URLs show the existing placeholder to avoid remote availability failures. Payment form reuses admin-catalog validation/busy/dirty states and shared voucher-preview. Receipt is a paper-like panel with dedicated A4 print media: no navigation or actions, repeated table header, rows kept together. Vietnamese numbers retain up to two decimal places when stored prices/caps include fractions.

## Phase 10 statistics
Statistics retains the Admin shell, native date/channel controls and URL filters. Three numeric summaries, fixed-height Chart.js charts and semantic tables provide equivalent numeric access. Top products reuse catalog thumbnails and fallback. Two navigation links preserve committed filters; empty ranges omit chart canvases. No new identity or token owner.

## Phase 11 visual reconciliation
User and Admin keep their existing identity and workflows. Orange darkens from #f05d23 to #b94312 to give white button text at least 4.5:1; muted text becomes #59636f. design-system.css is the shared token/storefront component owner; admin.css owns all Admin components including POS, receipts, charts and print. No screen-specific CSS files or literal inline styles remain. Responsive tables scroll locally; images use fixed frames/object-fit:contain. Storefront orders reuse the User shell. store-ui.js owns User validation/loading/messages and its native confirmation dialog; admin-catalog.js retains Admin ownership. Neither GET filter form creates an unsaved-data warning. Runtime Chart.js sizing and shared textarea/image-preview sizing remain generated styles, not duplicated static CSS.

## Admin account and POS held carts
Account uses Admin panels, Bootstrap definition-list columns and shared field errors; no page stylesheet or token changes. Topbar exposes Account and a CSRF-protected POST Logout. POS held carts use semantic buttons, existing panel/dialog owners and Bootstrap warning tones. Held cards display current estimated prices and expiry countdown; estimates never reserve inventory. A native dialog business variant owns save/discard/cancel when switching baskets. Server rules and evidence are documented in docs/ADMIN_ACCOUNT_POS_HOLD_REPORT.md.
