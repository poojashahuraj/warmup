typedef i64 long
typedef i32 int
service add {  // defines simple arithmetic service
            long addition(1:int num1, 2:int num2),
            long multiply(1:int num1, 2:int num2),
}
