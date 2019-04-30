import sys

def text_to_bin(string):
    result = ""
    for character in string:
        result += bin(ord(character))[2:].zfill(8)
    return result

# s1, s2
sig_encrpyt = sys.argv[1]
text_encrpyt = sys.argv[2]
# a1 xor a2 = s1 xor s2
xor_string = int(sig_encrpyt, 2) ^ int(text_encrpyt, 2)
s1_xor_s2 = bin(xor_string)[2:].zfill(len(sig_encrpyt))
# signature: Sent by BarryBot, School of Computer Science, The University of Manchester
signature = text_to_bin("Sent by BarryBot, School of Computer Science, The University of Manchester")
# random text = s1 xor s2 xor signature
# print(signature)
text_bin = int(s1_xor_s2, 2) ^ int(signature, 2)
text = bin(text_bin)[2:].zfill(len(signature))
for i in range(0, len(text)-7, 8):
    character = text[i: i+8]
    n = chr(int(character, 2))
    print(n, end="")

print()