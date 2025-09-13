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