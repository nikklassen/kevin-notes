library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity SevenSegment is port (

	dataIn		:	in		std_logic_vector(3 downto 0);
	blanking	:	in		std_logic;
	
	segmentsOut	:	out		std_logic_vector(6 downto 0)
);

end SevenSegment;

architecture Behavioral of SevenSegment is

begin

	with blanking & dataIn select
		segmentsOut(6 downto 0) <=	"1000000" when "00000",
									"1111001" when "00001",
									"0100100" when "00010",
									"0110000" when "00011",
									"0011001" when "00100",
									"0010010" when "00101",
									"0000010" when "00110",
									"1111000" when "00111",
									"0000000" when "01000",
									"0010000" when "01001",
									"0001000" when "01010",
									"0000011" when "01011",
									"0100111" when "01100",
									"0100001" when "01101",
									"0000110" when "01110",
									"0001110" when "01111",
									"1111111" when others; 

end Behavioral;

entity Lab2 is port (

	sw			:	in		std_logic_vector(17 downto 0);

	ledr		:	out std_logic_vector(17 downto 0);
	hex0, hex1, hex2, hex3, hex4, hex5, hex6, hex7	:	out	std_logic_vector(6 downto 0)  -- 7-segment desplays
);

end Lab2;

architecture Calculator of Lab2 is

	component SevenSegment port (

		dataIn		:	in		std_logic_vector(3 downto 0);
		blanking	:	in		std_logic;

		segmentsOut	:	out		std_logic_vector(6 downto 0)
	);

	end component;

	signal op1:		std_logic_vector(7 downto 0);
	signal op2:		std_logic_vector(7 downto 0);
	signal op:		std_logic_vector(1 downto 0);
	
	signal result:	std_logic_vector(8 downto 0);

begin

	op1 <= sw(7 downto 0);
	op2 <= sw(15 downto 8);
	op <= sw(17 downto 16);

	with op select
		result <=	"0" & op1 and op2										when "00",
					"0" & op1 or op2										when "01",
					"0" & op1 xor op2										when "10",
					std_logic_vector(unsigned("0"&op1) + unsigned("0"&op2))	when "11";

	ledr(17 downto 16)	<= op;
	ledr(15 downto 9)	<= "0000000";
	ledr(8 downto 0)	<= result;


	D7SH0: SevenSegment port map(result(3 downto 0),	'0', hex0 );
	D7SH1: SevenSegment port map(result(7 downto 4),	'0', hex1 );
	D7SH2: SevenSegment port map("000"&result(8),		not result(8), hex2 );
	D7SH3: SevenSegment port map("0000",				'1', hex3 );

	D7SH4: SevenSegment port map(op1(3 downto 0), '0', hex4 );
	D7SH5: SevenSegment port map(op1(7 downto 4), '0', hex5 );

	D7SH6: SevenSegment port map(op2(3 downto 0), '0', hex6 );
	D7SH7: SevenSegment port map(op2(7 downto 4), '0', hex7 );

end Calculator;