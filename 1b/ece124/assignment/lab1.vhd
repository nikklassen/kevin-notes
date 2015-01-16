library ieee;
use ieee.std_logic_1164.ALL;

entity Lab1 is port (

		key	:	in	std_logic_vector(2 downto 0);
		sw		:	in	std_logic_vector(1 downto 1);

		ledr	:	out	std_logic_vector(0 downto 0);
		ledg	:	out	std_logic_vector(0 downto 0)
);

end Lab1;

architecture CarControl of Lab1 is

	signal gas, clutch, brake, override: std_logic;
	signal gasControl, brakeControl: std_logic;

begin

	gas <= not key(0);
	clutch <= not key(1);
	brake <= not key(2);
	override <= sw(1);

	with override select
		gasControl	<=	gas and not clutch and not brake		when '0',
							'0'											when '1';

	brakeControl <= clutch or brake or override;

	ledr(0) <= brakeControl;
	ledg(0) <= gasControl;

end CarControl;