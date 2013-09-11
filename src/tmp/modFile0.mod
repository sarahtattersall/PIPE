\model{
	\statevector{
		\type{short}{P0, P1, P2, tagged_location}
	}

	\initial{
		P0 = 2; P1 = 0; P2 = 0; tagged_location = -1;
	}
	\transition{T0}{
		\condition{P0 > 0}
		\action{
			next->P0 = P0 - 1;
			next->P1 = P1 + 1;
		}
		\rate{1.0}
	}
	\transition{T1}{
		\condition{P1 > 0}
		\action{
			next->P1 = P1 - 1;
			next->P2 = P2 + 1;
		}
		\rate{1.0}
	}
	\transition{T2}{
		\condition{P2 > 0}
		\action{
			next->P2 = P2 - 1;
			next->P0 = P0 + 1;
		}
		\rate{1.0}
	}
}

\solution{
	\method{sor}

}