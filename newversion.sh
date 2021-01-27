#!/bin/bash


if [ "$VERSION" = "" ] ; then
	read -p "version?> " VERSION;
	read -p "confirmation?> " VERSION2;
else
	VERSION2="$VERSION";
fi
if [ "$VERSION" = "" ] ; then
	echo "Empty version";
	exit 1;
fi
if ! [ "$VERSION" = "$VERSION2" ] ; then
	echo "$VERSION != $VERSION2";
	exit 1;
fi
F="./app/build.gradle";
T="$F.temp";
sed "s/\(versionName \"\)[^\"]\+/\1${VERSION}/" $F > $T;
mv $T $F;
git add $F;
git commit -m "Version $VERSION";
git tag $VERSION
git push origin HEAD:master
git push --tags origin HEAD:master
