acceptedStates = [ "ID", "NUM", "FINAL", "EQUALS" ]
allStates = [ "START", "ID", "NUM", "FINAL", "EQUALS", "BANG", "ERROR" ]
alphabet = [ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "{", "}", "(", ")", "[", "]", "=", "+", "-", "*", "/", "&", "<", ">", "!", ";", ",", "%"]


print(len(alphabet))
for alpha in alphabet:
	print(alpha)


print(len(allStates))
for token in allStates:
	print(token)

print("START")


print(len(acceptedStates))
for token in acceptedStates:
	print(token)


print(len(allStates) * len(alphabet))
for token in allStates:
	for alpha in alphabet:
		if token == "START":
			if alpha == "0" or alpha == "/" or alpha == "[" or alpha == "]" or alpha == "{" or alpha == "}" or alpha == "(" or alpha == ")" or alpha == "+" or alpha == "-" or alpha == "*" or alpha == "%" or alpha == "," or alpha == ";" or alpha == "&":
				print("START " + alpha + " FINAL")
			elif alpha == "a" or alpha == "b" or alpha == "c" or alpha == "d" or alpha == "e" or alpha == "f" or alpha == "g" or alpha == "h" or alpha == "i" or alpha == "j" or alpha == "k" or alpha == "l" or alpha == "m" or alpha == "n" or alpha == "o" or alpha == "p" or alpha == "q" or alpha == "r" or alpha == "s" or alpha == "t" or alpha == "u" or alpha == "v" or alpha == "w" or alpha == "x" or alpha == "y" or alpha == "z" or alpha == "A" or alpha == "B" or alpha == "C" or alpha == "D" or alpha == "E" or alpha == "F" or alpha == "G" or alpha == "H" or alpha == "I" or alpha == "J" or alpha == "K" or alpha == "L" or alpha == "M" or alpha == "N" or alpha == "O" or alpha == "P" or alpha == "Q" or alpha == "R" or alpha == "S" or alpha == "T" or alpha == "U" or alpha == "V" or alpha == "W" or alpha == "X" or alpha == "Y" or alpha == "Z":
				print("START " + alpha + " ID")
			elif alpha == "1" or alpha == "2" or alpha == "3" or alpha == "4" or alpha == "5" or alpha == "6" or alpha == "7" or alpha == "8" or alpha == "9":
				print("START " + alpha + " NUM")
			elif alpha == "=" or alpha == "<" or alpha == ">":
				print("START " + alpha + " EQUALS")
			elif alpha == "!":
				print("START ! BANG")

		if token == "ID":
			if alpha == "a" or alpha == "b" or alpha == "c" or alpha == "d" or alpha == "e" or alpha == "f" or alpha == "g" or alpha == "h" or alpha == "i" or alpha == "j" or alpha == "k" or alpha == "l" or alpha == "m" or alpha == "n" or alpha == "o" or alpha == "p" or alpha == "q" or alpha == "r" or alpha == "s" or alpha == "t" or alpha == "u" or alpha == "v" or alpha == "w" or alpha == "x" or alpha == "y" or alpha == "z" or alpha == "A" or alpha == "B" or alpha == "C" or alpha == "D" or alpha == "E" or alpha == "F" or alpha == "G" or alpha == "H" or alpha == "I" or alpha == "J" or alpha == "K" or alpha == "L" or alpha == "M" or alpha == "N" or alpha == "O" or alpha == "P" or alpha == "Q" or alpha == "R" or alpha == "S" or alpha == "T" or alpha == "U" or alpha == "V" or alpha == "W" or alpha == "X" or alpha == "Y" or alpha == "Z" or alpha == "0" or alpha == "1" or alpha == "2" or alpha == "3" or alpha == "4" or alpha == "5" or alpha == "6" or alpha == "7" or alpha == "8" or alpha == "9":
				print("ID " + alpha + " ID")
			else:
				print("ID " + alpha + " ERROR")

		elif token == "NUM":
			if alpha == "0" or alpha == "1" or alpha == "2" or alpha == "3" or alpha == "4" or alpha == "5" or alpha == "6" or alpha == "7" or alpha == "8" or alpha == "9":
				print("NUM " + alpha + " NUM")
			else:
				print("NUM " + alpha + " ERROR")

		elif token == "FINAL":
			print("FINAL " + alpha + " ERROR")

		elif token == "EQUALS":
			if alpha == "=":
				print("EQUALS = FINAL")
			else:
				print("EQUALS " + alpha + " ERROR")

		elif token == "BANG":
			if alpha == "=":
				print("BANG = FINAL")
			else:
				print("BANG " + alpha + " ERROR")

		elif token == "ERROR":
			print("ERROR " + alpha + " ERROR")
