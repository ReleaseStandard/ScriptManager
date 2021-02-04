#
# If nothing is sayed then compatibility has not been checked.
# If class is compat x then all methods are compat level x.
# W: For compat not recursive checks: e.g. class A extends B (B compat 10, A compat 1)
#    ~All basic blocks of the class are compat level x
find app/src/main/java/ -name "*.java" -exec bash -c 'grep "* compat" {} | sed "s/^ \+\* compat //g"' \; |sort -u -n|tail -n 1
