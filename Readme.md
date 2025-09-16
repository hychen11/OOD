# OOD template

Step 1: Requirements Clarification

Functional requirements

Non-functional requirements

* Scalibility
* latency

Step 2: Identify Core Entities


Step 3: Define Relationships

Step 4: Define Interfaces & Methods

Step 5: Discuss Extensions & Trade-offs

Step 6: Wrap Up

### **Step 1. Clarify 需求 (Requirement Clarification)**

我先确认一下需求：

1. 用户下单 → 系统要给用户分配一个合适的 locker，并生成 PIN。
2. 用户收到通知，凭 PIN 去取件 → Locker 打开 → 用户取走包裹。
3. Locker 有不同的尺寸（Small, Medium, Large），要和包裹匹配。
4. Locker 有不同状态（空闲、占用、损坏）。
5. 管理员可以维护 locker，比如修理、清空等。

可选需求：

- 如果 locker 满了怎么办？
- 是否支持多个站点？
- PIN 的过期时间？

### **Step 2. Identify 核心对象 (Entities & Classes)**

我认为系统的核心对象有：

- **Package**：包裹，包含 `id`、`size`。
- **Locker**：储物柜格子，包含 `id`、`size`、`status`、`package`。
- **LockerManager**：管理所有 locker，负责分配 locker、生成 PIN。
- **User**：用户，收到通知后凭 PIN 取件。
- **NotificationService**：发送取件 PIN 给用户。

### **Step 3. 定义关系 (Relationships)**

- 一个 **LockerManager 管理多个 Locker**。
- 一个 **Locker 可以存放一个 Package**。
- 一个 **Package 会被分配到一个合适的 Locker**。
- **NotificationService** 与 **User** 交互，发送 PIN。

### **Step 4. 定义接口和方法 (Methods)**

### Locker

- `isAvailable(): boolean`
- `assignPackage(Package pkg): void`
- `releasePackage(): Package`

### LockerManager

- `assignLocker(Package pkg): Locker` → 找到合适尺寸的空闲 locker。
- `generatePin(Locker locker): String` → 生成 PIN，存储映射。
- `openLocker(String pin): void` → 验证 PIN，打开 locker。

### User

- `pickupPackage(String pin): void`

### NotificationService

- `sendNotification(User user, String pin): void`

### **Step 5. 处理扩展性 & 特殊情况 (Extensions & Edge Cases)**

- **Locker 满了**：返回“无可用 locker”，可以放到最近的其他站点。
- **Locker 损坏**：Locker 有 `status` 字段（AVAILABLE, OCCUPIED, BROKEN），分配时跳过。
- **多站点**：LockerManager 可以管理多个 `LockerLocation`。
- **PIN 过期**：在数据库里存储 `pin → lockerId` 映射，带上过期时间，过期自动清理。

### **Step 6. 总结 (Wrap Up)**

总结一下：

- 我先确认了需求，系统的目标是 **把包裹存进合适的 locker 并支持用户取件**。
- 我抽象出核心对象：Package、Locker、LockerManager、User、NotificationService。
- 定义了对象关系和关键方法，比如 `assignLocker`、`generatePin`、`openLocker`。
- 最后我考虑了扩展性，比如 locker 满了、损坏、多站点管理、PIN 过期。

这样设计的好处是：

- **职责清晰**（LockerManager 管理 locker，Locker 存包裹，User 取件）。
- **扩展性强**（可以方便增加新功能，比如智能推荐 locker、支持 QR 码取件）。
- **易维护**（locker 状态、分配逻辑独立，不影响其他模块）。



## parkinglot

停车场由若干层（Level）与若干车位（ParkingSpot）组成。支持不同车辆类型（Motorcycle、Car、Bus）与不同车位尺寸（Motorcycle, Compact, Large）。系统需支持：

- 停车（park）与取车（unpark）并返回 Ticket（包含车位信息）。
- 能查找可用车位数/状态。
- 支持并发场景（尽量无全局阻塞），并且当策略有变化时容易扩展（例如从“第一个空位”改成“优先地面层”）。
- 要尽量遵循 SOLID 并使用合适设计模式。

Functional requirements:

- The parking lot has multiple parking spots, including compact, regular, and oversized spots.
- The parking lot supports parking for motorcycles, cars, and trucks.
- Customers can park their vehicles in spots assigned based on vehicle size.
- Customers receive a parking ticket with vehicle details and entry time at the entry point and pay a fee based on duration, vehicle size, and time of day at the exit point.

Non-functional requirements:

- The system must scale to support large parking lots with many spots and vehicles.
- The system must reliably track spot assignments and ticket details to ensure accurate operations.

## amazon locker

https://leetcode.com/discuss/post/233869/design-amazon-locker-system-by-anonymous-lgpn/

#### Given

- Users should be able to use a code to open a locker and pick up a package
- Delivery guy should be able to find an "optimal" locker for a package

#### Then

- Free coding from scratch in any language

Functional requirements

* different package size (large, medium, small)
* different locker size (large, medium, small)
* Code record the lockId, userId

Non-functional requirements

* scalibility





userInterface

* user PIN code to fetch package
* send package (for delivery personnels)

LockerManager 

* Manages all lockers of different sizes and their status

* Allocate locker according to package size

* Generate code otp (generate one-time-password when store)

* Deallocate when fetch package using otp

   



```c++

```

## Linux file search

```c++

```

## Pizza

```c++

```

## elevator

```c++

```