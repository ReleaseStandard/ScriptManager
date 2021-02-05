#
# If nothing is sayed then compatibility has not been checked.
# If class is compat x then all methods are compat level x.
# W: For compat not recursive checks: e.g. class A extends B (B compat 10, A compat 1)
#    ~All basic blocks of the class are compat level x


t="/tmp/t";
g="./app/build.gradle";
l=$(find app/src/main/java/ -name "*.java" -exec bash -c 'grep "* compat" {} | sed "s/^ \+\* compat \\([0-9]\\+\\).*/\1/g"' \; |sort -u -n|tail -n 1)
echo "$l";

if [ "$1" = "write" ] ; then
	echo "Modify build.gradle";
	sed "s/\\(minSdkVersion \\+\\)[0-9]\\+/\\1${l}/g" $g > $t;
	mv $t $g;
fi

# Currently studio say 16 min maybe
