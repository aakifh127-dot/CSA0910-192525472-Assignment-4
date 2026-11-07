#!/bin/sh
# Task 5.2: measured effort of the changed requirement (Enter key + dialog box) for each style.
# The v2 file is first renamed back (class name and title) so that only real code changes are counted.
cd "$(dirname "$0")/src"
tmp=$(mktemp)
printf "%-38s %-12s %-14s %-12s\n" "Style" "Lines added" "Lines removed" "Total changed"
for s in A B C D; do
  case $s in A) n="(a) frame implements ActionListener";; B) n="(b) named inner class";; C) n="(c) anonymous inner class";; D) n="(d) lambda expression";; esac
  sed "s/Converter${s}2/Converter${s}/g; s/ (v2)//" Converter${s}2.java > "$tmp"
  add=$(diff Converter$s.java "$tmp" | grep -c '^>')
  del=$(diff Converter$s.java "$tmp" | grep -c '^<')
  printf "%-38s %-12s %-14s %-12s\n" "$n" "$add" "$del" "$((add+del))"
done
rm -f "$tmp"
echo "(each count includes the 1-2 new import lines needed for the dialog box / ActionListener)"
echo "Lines of code per version (v1 -> v2):"
for s in A B C D; do echo "  Converter$s: $(wc -l < Converter$s.java) -> $(wc -l < Converter${s}2.java)"; done
