# CLAUDE.md — Hướng dẫn làm việc cho Claude Code trong repo mini-banking

> File này được Claude Code tự đọc ở mọi session. Ghi lại cách mình (user) và Claude làm việc với nhau,
> để session nào cũng làm giống nhau. `README.md` là README portfolio — không dùng cho mục đích này.

## Bối cảnh

- Đây là repo **học tập** theo roadmap apply Backend Engineer (Mid) — xem roadmap trên Notion:
  https://app.notion.com/p/Roadmap-apply-NAB-Backend-Engineer-Mid-6-th-ng-416a572ea950816a863afe9ffe6942be
- **Lý thuyết học bên Claude chat (claude.ai).** Trong Claude Code: mình mang ticket sang, tự code,
  Claude review + viết test + làm git khi được nhờ. **Không tự dạy lý thuyết** nếu mình không hỏi.
- Giao tiếp bằng **tiếng Việt**. Code, tên class, commit message bằng **tiếng Anh**.
- Stack: Java 17, Maven multi-module, JUnit 5 + AssertJ. Module hiện có: `account-service`.

## Cấu trúc bài học

| Bài | Package | Ticket |
|---|---|---|
| Bài N trong roadmap Phase 1 | `account-service/src/main/java/com/minibank/lessonN` | `ACC-0N` |
| Test của bài N | `account-service/src/test/java/com/minibank/lessonN` | |

Thứ tự Phase 1: 1 OOP · 2 Encapsulation · 3 Inheritance/Composition · 4 Polymorphism/Abstraction ✅ (ACC-04) ·
5 SOLID S,O · 6 SOLID L,I,D · 7 Collections · 8 Generics · 9 Stream/Lambda · 10 Exception · 11 equals/hashCode ·
12 Immutability · 13 Java 17+ · 14–16 Concurrency · 17 JVM → mini project Cinema seat booking.

## Quy trình một ticket

1. **Nhận ticket** (mình dán đề bài). Claude **chỉ gợi ý, không viết lời giải** — trừ `Main.java`
   (scenario demo) nếu mình nhờ. Gợi ý theo 3 cấp khi mình xin: 💡 hướng đi → 🧱 skeleton → 🧩 code mẫu phần khó.
   Với bài thiết kế: đưa 2–3 option kèm code sketch ngắn, mình chọn và giải thích, Claude nhận xét trade-off.
2. **Branch:** `feature/ACC-xx-<mo-ta-ngan>` tạo từ `master` (Claude tạo nếu mình nhờ).
3. **Review** khi mình báo "xong":
   - Đọc lại toàn bộ file của bài, **compile thử** trước khi nhận xét.
   - Chia nhận xét: ❌ lỗi compile / ⚠️ sai yêu cầu ticket / 💡 nhỏ nên biết / ✅ đã ổn.
   - Hay gặp: copy-paste sai message/icon, validate thiếu, method còn "biết" chi tiết lẽ ra phải ẩn,
     code trùng lặp giữa các subclass, `System.out` thừa trong logic nghiệp vụ.
   - **Không tự sửa code của mình** — chỉ ra để mình sửa. Ngoại lệ: việc vặt mình nhờ thẳng (vd thêm emoji).
4. **Main.java:** scenario in rõ `before / after / expected` và ✅/❌, có case thành công + case bị từ chối.
5. **Test** (Claude tự viết, mình không viết test lúc học):
   - JUnit 5 + AssertJ, mỗi class một `XxxTest`, đầu file ghi:
     `// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session`
   - Dùng fake implementation (vd `RecordingNotifier`) thay vì mock framework — chưa học Mockito.
   - Báo kết quả 1 dòng: số pass/fail + bug nào test phát hiện. Không giảng giải test.
6. **Câu hỏi bonus** sau mỗi ticket: một tình huống backend thực tế (edge case, production, idempotency…).
   Nếu vượt roadmap hiện tại thì ghi vào mục Backlog bên dưới, để sau.

## Git (chỉ làm khi mình nhờ)

- Conventional commits, scope là mã ticket: `feat(ACC-04): ...`, `fix(...)`, `refactor(...)`, `test(...)`.
- **Tách commit theo từng phần của ticket**, mỗi commit phải compile được. Thứ tự thường:
  model/logic → service/interface → `Main` scenarios → tests.
- Ticket dạng 🔥 production incident: **ghi root cause vào body** của commit `fix(...)`.
- Kết thúc commit message bằng:
  `Co-Authored-By: Claude <noreply@anthropic.com>`
- Push: `git push -u origin feature/ACC-xx-...`
- Merge: `git checkout master && git merge --ff-only feature/ACC-xx-...` rồi `git push origin master`
  (lịch sử thẳng, không merge commit). Không xóa branch nếu mình không nhờ.
- Trước khi commit/merge luôn chạy test toàn module.

## Lệnh hay dùng

```bash
./mvnw -pl account-service test                                   # toàn bộ test
./mvnw -pl account-service test -Dtest='com.minibank.lesson4.*Test' # test 1 bài
# chạy Main của 1 bài mà không cần build Maven:
javac -d /tmp/out account-service/src/main/java/com/minibank/lesson4/*.java && java -cp /tmp/out com.minibank.lesson4.Main
```

## Backlog (để dành, chưa làm)

- **Tạm khóa tài khoản nợ phí** (từ bonus ACC-04, hợp với Bài 10 – Exception): `BusinessAccount` không đủ tiền
  trả phí làm `endOfMonth()` throw → cả `MonthEndJob` dừng, chạy lại thì trừ trùng tài khoản đã xử lý (idempotency).
  Hướng đã chốt: thiếu tiền = tình huống nghiệp vụ → khóa tài khoản + ghi nợ, nạp tiền thì trừ nợ rồi mở khóa;
  job try/catch từng account và trả về tổng kết `Processed / Suspended / Failed`.
  Bước tiếp: mình gửi 4–5 câu hỏi làm rõ cho Claude (đóng vai PO) → chốt acceptance criteria → code.
