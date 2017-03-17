namespace java com.colobu.thrift
namespace go main

struct BenchmarkMessage
{
  1:  string field1,
  2:  i32 field2,
  3:  i32 field3,
  4:  string field4,
  5:  i64 field5,
  6:  i32 field6,
  7:  string field7,
  9:  string field9,
  12:  bool field12,
  13:  bool field13,
  14:  bool field14,
  16:  i32 field16,
  17:  bool field17,
  18:  string field18,
  22:  i64 field22,
  23:  i32 field23,
  24:  bool field24,
  25:  i32 field25,
  29:  i32 field29,
  30:  bool field30,
  59:  bool field59,
  60:  i32 field60,
  67:  i32 field67,
  68:  i32 field68,
  78:  bool field78,
  80:  bool field80,
  81:  bool field81,
  100:  i32 field100,
  101:  i32 field101,
  102:  string field102,
  103:  string field103,
  104:  i32 field104,
  128:  i32 field128,
  129:  string field129,
  130:  i32 field130,
  131:  i32 field131,
  150:  i32 field150,
  271:  i32 field271,
  272:  i32 field272,
  280:  i32 field280,
}


service Greeter {

    BenchmarkMessage say(1:BenchmarkMessage name);

}