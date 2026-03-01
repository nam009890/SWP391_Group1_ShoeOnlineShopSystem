cấu trúc của project: (ae chuyển sang code để xem cái này) 
ShoeShopOnline/
├── pom.xml                                 <-- File cấu hình Maven (chứa các thư viện Spring Web, Thymeleaf, JPA, SQL Server, Lombok...)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fpt/shoeshop/           <-- Base package của dự án
│   │   │       ├── ShoeShopApplication.java<-- File chạy dự án (Main)
│   │   │       │
│   │   │       ├── config/                 <-- Cấu hình Spring Security, WebMvc, Interceptor...
│   │   │       ├── controller/             <-- Tiếp nhận Request từ người dùng (chia theo Role/Feature)
│   │   │       │   ├── admin/
│   │   │       │   ├── marketing/
│   │   │       │   ├── sale/
│   │   │       │   ├── customer/
│   │   │       │   └── auth/               <-- Login, Register
│   │   │       │
│   │   │       ├── dto/                    <-- Data Transfer Object (Chứa các class Form upload, Request, Response)
│   │   │       │
│   │   │       ├── entity/                 <-- Các class map trực tiếp với các bảng trong SQL (User, Product, Order...)
│   │   │       │
│   │   │       ├── repository/             <-- Chứa các interface kế thừa JpaRepository để gọi DB
│   │   │       │
│   │   │       ├── service/                <-- Chứa interface xử lý logic nghiệp vụ
│   │   │       │   └── impl/               <-- Class implements các interface service
│   │   │       │
│   │   │       ├── security/               <-- Xử lý phân quyền (CustomUserDetails, Jwt nếu có)
│   │   │       │
│   │   │       └── util/                   <-- Các hàm dùng chung (Upload file, Format ngày tháng, Random code...)
│   │   │
│   │   └── resources/
│   │       ├── application.properties      <-- Cấu hình kết nối SQL Server, port chạy app, Thymeleaf cache...
│   │       │
│   │       ├── static/                     <-- Chứa tài nguyên tĩnh (Frontend)
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   ├── images/
│   │       │   │   ├── products/
│   │       │   │   ├── sliders/
│   │       │   │   └── avatars/
│   │       │   └── plugins/                <-- Bootstrap, FontAwesome, jQuery...
│   │       │
│   │       └── templates/                  <-- Chứa các file giao diện Thymeleaf (.html)
│   │           ├── layout/                 <-- Header, Footer, Sidebar dùng chung để include vào các trang khác
│   │           ├── admin/                  <-- Hoàng code giao diện ở đây
│   │           ├── marketing/              <-- Hiếu code giao diện ở đây
│   │           ├── sale/                   <-- Sơn code giao diện ở đây
│   │           ├── customer/               <-- Minh code giao diện ở đây
│   │           └── auth/                   <-- Nam code giao diện Login, Register ở đây
