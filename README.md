<div align="center">
  <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    <img src="https://github.com/user-attachments/assets/77fe0fd1-2e55-4032-be3c-b1a705a1b574" alt="DaiNam University Logo" width="200"/>
  </a>
</div>

# HỆ THỐNG QUẢN LÝ NGÂN HÀNG MINI

<div align="center">
  <a href="https://www.facebook.com/DNUAIoTLab">
    <img src="https://img.shields.io/badge/AIoTLab-green?style=for-the-badge" alt="AIoTLab"/>
  </a>
  <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    <img src="https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge" alt="Faculty of IT"/>
  </a>
  <a href="https://dainam.edu.vn">
    <img src="https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge" alt="DaiNam University"/>
  </a>
</div>

## 1. Giới thiệu hệ thống
**Hệ thống quản lý ngân hàng mini** là một ứng dụng cho phép người dùng quản lý tài khoản ngân hàng và thực hiện các giao dịch tài chính cơ bản thông qua giao diện đồ họa. Hệ thống hỗ trợ hai vai trò chính:

- **Admin**:
  - Tạo và xóa tài khoản ngân hàng.
  - Xem danh sách tất cả tài khoản.
  - Xem thống kê tổng số tài khoản và tổng số dư.
  - Xem toàn bộ lịch sử giao dịch của hệ thống.
- **Customer**:
  - Đăng ký tài khoản và đăng nhập.
  - Nạp tiền, rút tiền, và chuyển tiền giữa các tài khoản.
  - Xem số dư và lịch sử giao dịch cá nhân.

### Kiến trúc hệ thống
- **Client**: Giao diện người dùng được xây dựng bằng **Java Swing**, cung cấp các chức năng cho Admin và Customer.
- **Server**: Xử lý yêu cầu từ client, quản lý dữ liệu trong **MySQL**, sử dụng **Java RMI** để giao tiếp client-server.
- **Cơ sở dữ liệu**: Lưu trữ thông tin người dùng (`users`), tài khoản (`accounts`), và giao dịch (`transactions`).

**Đặc điểm nổi bật**:
- Giao diện trực quan, dễ sử dụng.
- Đảm bảo an toàn giao dịch với kiểm tra số dư trước khi rút hoặc chuyển tiền.
- Hỗ trợ lưu trữ và truy xuất lịch sử giao dịch chi tiết.

## 2. Ngôn ngữ & Công nghệ
- [![Java](https://img.shields.io/badge/Java%208-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
- [![Java RMI](https://img.shields.io/badge/Java%20RMI-007396?style=for-the-badge)](https://docs.oracle.com/javase/8/docs/technotes/guides/rmi/)
- [![Java Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge)](https://docs.oracle.com/javase/tutorial/uiswing/)
- [![MySQL](https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

## 3. Một số màn hình giao diện
<div align="center">
  <img src="images/login.png" alt="Giao diện đăng nhập" width="500"/>
  <p><em>Hình 1: Giao diện đăng nhập/đăng ký</em></p>
</div>

<div align="center">
  <img src="images/admin.png" alt="Giao diện admin" width="500"/>
  <p><em>Hình 2: Giao diện quản lý tài khoản (Admin)</em></p>
</div>

<div align="center">
  <img src="images/customer.png" alt="Giao diện khách hàng" width="500"/>
  <p><em>Hình 3: Giao diện giao dịch (Customer)</em></p>
</div>

> **Lưu ý**: Thay thế các ảnh trên bằng ảnh chụp màn hình thực tế của ứng dụng.

## 4. Cài đặt & Sử dụng
### Yêu cầu môi trường
- **JDK**: Java Development Kit 8 trở lên.
- **MySQL**: MySQL Server 8.0 trở lên.
- **IDE**: Eclipse, IntelliJ IDEA, hoặc chạy trực tiếp qua terminal.
### Cấu trúc cơ sở dữ liệu
Tạo cơ sở dữ liệu và các bảng bằng tệp `bank_system.sql`:
## 5. Thông tin liên hệ
Sinh viên: Nguyễn Tiến Thái  
Lớp: CNTT 16-01  
Email: tienthai0915@gmail.com  
GitHub: github.com/<your-username>mmr-09</your-username>  

© 2025 AIoTLab – Faculty of Information Technology, DaiNam University
