from=${1:-"$HOME/Downloads/react-icons-xmls"}
to=app/src/main/res/drawable

supportedIcons=("fa" "md")

for pac in "${supportedIcons[@]}";do
    cp -v "$from"/"${pac}"/*.xml $to
done