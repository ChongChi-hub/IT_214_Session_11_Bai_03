# BÀI TẬP 3: CƠ CHẾ FAN-OUT VÀ LẮNG NGHE SỰ KIỆN (EVENT CONSUMER)

## 1. Mục tiêu
- Ứng dụng mô hình Broker Topology (Choreography).
- Thiết lập Consumer Group để chia tải và phân phối sự kiện.

## 2. Báo cáo cấu hình và Xử lý lỗi

### 2.1. Thiết lập 2 ứng dụng Microservice
Đã khởi tạo hai module độc lập là `inventory-service` và `loyalty-service` đóng vai trò Consumer cùng lắng nghe chung một Topic `storex-order-events`. 

### 2.2. BUG-04: Lỗi dùng chung group-id gây ra mất message
**Nguyên nhân:** Lập trình viên trước đó đã cấu hình `group.id=storex-system` cho cả Kho và Điểm thưởng. Theo nguyên lý của Kafka Consumer Group, mỗi thông điệp trong một partition chỉ được chuyển đến **một consumer duy nhất** trong cùng một nhóm (Consumer Group). Do đó, Kho và Điểm thưởng cạnh tranh nhau, dẫn đến đơn hàng vào Kho thì sẽ không chạy vào Điểm thưởng.
**Cách xử lý:** Đã cấu hình lại `group-id` riêng biệt cho từng service trong file `application.yml` của chúng:
- `inventory-service`: `group-id: inventory-group`
- `loyalty-service`: `group-id: loyalty-group`
Với cấu hình này, Kafka sẽ triển khai cơ chế **Fan-out** (Publish-Subscribe). Cả hai nhóm Consumer sẽ đều nhận được 100% bản sao của luồng thông điệp đơn hàng một cách độc lập.

### 2.3. REQ-01: Thiết kế Partition cho Inventory-Service
**Yêu cầu:** Scale-up Inventory-Service lên 3 instance và đề xuất số lượng Partition tối thiểu.
**Phân tích:** Theo nguyên lý phân phối tải của Kafka, một Partition chỉ có thể được tiêu thụ bởi tối đa một Consumer instance trong cùng một Consumer Group tại cùng một thời điểm.
- Nếu Topic có 2 Partitions mà có 3 instance Inventory-Service, sẽ có 1 instance luôn ở trạng thái "Idle" (ngồi chơi không làm gì).
- Để cả 3 instance Inventory-Service hoạt động hết công suất (mỗi instance xử lý ~33% tải), Topic `storex-order-events` cần được tạo với **ít nhất 3 Partitions** (số Partitions >= số Consumer tối đa).
- Lệnh tạo topic tham khảo: `kafka-topics.sh --create --topic storex-order-events --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092`

## 3. Hướng dẫn chạy
Lần lượt di chuyển vào thư mục `inventory-service` và `loyalty-service` và chạy:
```bash
./mvnw spring-boot:run
```
Cả hai ứng dụng sẽ khởi chạy độc lập và cùng nhận chung sự kiện khi có đơn hàng được tạo.
