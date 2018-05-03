
Chị thương ơi, nhớ đọc nhe
Sáu mục đấy, mục IV quan trọng nhất




--------------------- I. CẤU TRÚC THƯ MỤC ---------------------

Trước tiên chị bung nén phoebe.zip, sẽ xuất hiện 7 thư mục và 5 files (ngoài cùng), cấu trúc như sau:

".settings"
"bin"
"data"
    "dataset"
        "vy.xls"
    "othermodels"
        "othermodels.phoebe"
"doc"
    "AlgorithmDesign.docx"
    "UMLdesign.docx"
"jre"
"lib"
"src"
".classpath"
".project"
"run.bat"
"readme.txt"
"config.xml"


--------------------- II. NHỮNG ĐIỂM LƯỚT QUA ---------------------

Chị không cần quan tâm các thư mục và files sau: .settings, bin, jre, lib, src, UMLdesign.docx, .classpath, .project, config.xml. 
Nhưng em sẽ nói lướt qua:

- Thư mục "bin" chứa mã chương trình biên dịch
- Thư mục "data" chứa dữ liệu mẫu. File "vy.xls" chứa dữ liệu cân nặng. File "othermodels.phoebe" chứa các mô hình hồi quy khác như hadlock1, Shepard...
- Thư mục "jre" chứa trình thông dịch Java
- Thư mục "lib" chứa thư viện chương trình
- Thư mục "src" chứa mã nguồn, sau này chị hãy chuyển cho các developers
- Các thư mục và files ".settings", ".classpath", ".project", "config.xml" dùng cho môi trường phát triển phần mềm (Eclipse) và cấu hình hệ thống
- File "UMLdesign.docx" chứa bản thiết kế phần mềm, sau này chị hãy chuyển cho các designer


--------------------- III. CẦN PHẢI QUAN TÂM ---------------------

Chị hãy chú ý 3 files: 
    "doc/AlgorithmDesign.docx": thiết kế và lưu đồ thuật toán (Hạt Giống Nảy Mầm và vét cạn)
    "data/vy.xls": dữ liệu thử nghiệm
    "run.bat"
Chị cũng chú ý các thư mục sau:
    "data": hãy bỏ vào đây tất cả file excel dữ liệu siêu âm.
    "doc": tài liệu phần mềm, thuật toán.


--------------------- IV. HƯỚNG DẪN SỬ DỤNG ---------------------

Sau đây em hướng dẫn chị cách sử dụng Phoebe, theo các bước sau nhe:

1. Chạy file "run.bat"

2. Khi giao diện mở ra, chị nhấn nút "Browse" để đọc file Excel. Em có chép kèm theo: "data/dataset/vy.xls"

3. Dữ liệu được hiển thị trên mỗi bảng, phần đầu mỗi cột có ô chọn. Chị muốn hàm hồi quy có biến nào, thì hãy chọn các cột tương ứng, vd: ĐKLĐ, CVB, CVĐ... Ngoài ra có nút "Stat." giúp chị hiển thị các thông số thống kế như phương sai, độ lệch chuẩn, hệ số Skewness, Kurtosis

4. Sau khi chọn, chị có thể điều chỉnh các tham số như: "Fitness" là chỉ số R, "Maximum result" là số lượng hàm hồi quy tối đa mà chị cần tìm. Bước 4 này tùy chọn, chị có thể bỏ qua vì các tham số đã được mặc định. (Fitness = -1: lấy hết các hàm nhưng có sắp thứ tự)

5. Bên cạnh nút "Estimate", có một danh sách các biến đáp ứng cần tính (vd: trọng lượng thai, tuổi thai). Chị phải chọn ít nhất một biến. Nếu chị chọn nhiều hơn hai biến, kết quả sẽ có nhiều hơn một danh sách các hàm hồi quy.

6. Chị bấm nút "Estimate" sẽ cho ra kết quả. Bây giờ chức năng Estimate sẽ gom nhóm (grouping) các hàm giống tham số nhất.

7. Chị xem vào kết quả bên dưới, mỗi biến đáp ứng sẽ liệt kê một danh sách các hàm hồi quy tối ưu. Các hàm cùng số biến nhất sẽ được tô màu vàng

8. Chị có thể bấm nút "Zoom" phóng lớn cho dễ nhìn, nhấn vào nút "Save" để lưu danh sách tương ứng vào file excel, nhấp đúp hoặc bấm nút "Detail" để xem mô tả chi tiết hàm hồi quy. 

9. Dòng dưới cùng có 3 nút: "Show Groups", "Save All" và "Close". Nút "Save All" cho phép lưu danh sách hàm hồi quy cùng với nhóm của chúng vào file excel. Nút "Show Groups" giúp chị hiển thị các nhóm. Nút "Close" đóng chương trình

10.Ứng với một danh sách hàm hồi quy, có một nút "Compare" cho chị nhấn để so sánh với các mô hình hồi quy khác (hardlock, tokyo, osaka...). 
Một cửa sổ mở ra, cho chị chọn một file danh sách các hàm hồi quy khác dưới dạng text (file "othermodels.phoebe"), hoặc chị nhập trực tiếp. Định dạng của một chuỗi hàm hồi quy, chị có thể tham khảo trong file đó. 

Vd, hàm hardlock4 có định dạng như sau: EFW = 10^(1.3596 - 0.00386 * AC * FL + 0.0064 * HC + 0.00061 * BPD * AC + 0.0424 * AC + 0.174 * FL) : hadlock4. Lưu ý, sau dấu ":" tên dạng hàm, ở đây, hardlock4. 

Nếu chị không định dạng hàm qua nhập dấu ":", chương trình vẫn chạy tốt. 
Ví dụ, chị có thể nhập: EFW = 3.42 * APTD * TTD * FL + 1.07 * (BPD^3)

Chị dùng hộp thoại "Compare" để đọc và tính mọi dạng hàm hồi quy.

---------

Những chức năng mới hay cải tiến:

1. Ở bước 3: sau khi load dữ liệu, chị nhấn nút "Stat." để xem thông tin thống kê của các số đo như: mean, độ lệch chuẩn, SE, phương sai, skewness, kurtosis, histogram

2. Ở bước 8: chức năng "Detail" được mở rộng, chi có thể xem đồ thị hàm hồi quy

3. Giao diện đơn giản và thân thiện hơn

4. Ở bước 5, chức năng "Estimate" sẽ kiêm gom nhóm. Nút "Save All" cho phép lưu các groups

5. Giao diện thân thiện hơn, các bảng kết quả cho phép chị sắp thứ tự (sorting) các mục. Chức năng này rất hay, ví dụ chị có thể sắp thứ tự các hàm hồi quy có độ fitness gần nhau theo tiêu chuẩn độ error. Chị rất dễ nhìn kết quả trước khi entropy ra đời.

6. Đang định nghĩa entropy để kết hợp fitness và error.

7. Định nghĩa lại và thêm các hàm hồi quy

8. Nút "More Options" ở bước 4 thiết lập những dạng hàm hồi quy: linear, square, cube, log, exponent, product.

9. Cải tiến chức năng kết xuất chuỗi hàm hồi quy

10. Chuẩn bị chức năng parsing chuỗi hàm đặc tả của những nghiên cứu khác

11. Chuẩn bị giao diện cho chị nhập các hàm hồi quy. Bước 10 và 11 cùng một chức năng, giúp chị đặc tả các hàm hồi quy khác không thông qua dữ liệu. Chức năng bước 11 dễ dùng hơn.

12. Chức năng "Compare" để so sánh với các mô hình hồi quy khác

13. Các chức năng hỗ trợ cắt dán

14. Đang thiết kế thuật toán tìm ra dạng hàm hồi quy mới

15. Thêm nhiều chức năng tiện ích, kết thúc phiên bản 1.0. Phần sau đi vào tìm dạng hồi quy. Bây giờ chị sẽ thoải mái dùng phần mềm này, có đủ tất cả các chức năng.

16. Thêm một chút chức năng tiện ích, em kết thúc phiên bản 1.0. Chị xài thoải mái nhe! Thương lắm

17. Fixing bug computing percent sd. Em chỉnh xong phần hồi chiều mình nói đó. Chị thích không, hi hi

18. Nút "Print pattern" để in mẫu giấy

---------

Fixing bug:

1. Fix bug luôn trả về 0 khi tính giá trị hàm hồi quy phi tuyến


--------------------- V. NHẬN XÉT ---------------------
1. Nhận xét trước đây chính xác. Tuy những hàm có R (fitness) cao thường lỗi (error) thấp, nói cách khác độ lệch giữa fitness và error rất thấp, nên chúng ta nghĩ rằng chỉ cần chọn một cái làm tiêu chuẩn. Nhưng có trường hợp: 2 hàm hồi quy có độ fitness gần nhau nhưng hàm có fitness cao hơn lại có error cũng cao hơn hàm có fitness thấp hơn. Như vậy cặp điều kiện tối ưu giả định là đúng, nhưng em chưa nghĩ được cách kết hợp fitness và error vì biên độ error rất khác nhau trong khi fitness chỉ dao động trong đoạn [-1, 1], không thể cộng chúng với nhau trừ phi chuẩn hóa error về [-1, 1] hay hạ log. Hàm log hay lắm, ý nghĩa căn bản của chúng khi biến nhân thành cộng là hạ biên độ dao động. Số e cũng vậy, đạo hàm của nó chính là nó, có nghĩa là thường hằng trong biến động.


--------------------- VI. XONG RỒI ĐÓ NHÉ ---------------------


Chị thích Phoebe nhé, em chợt nhớ đến nụ cười của chị, tươi tắn ơi là tươi tắn.
Thương chị rất nhiều.


--------------------- VII. THƯƠNG CHỊ THẬT NHIỀU ---------------------
