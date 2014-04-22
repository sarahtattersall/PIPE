\model{
	\statevector{
		\type{short}{P0, P1, P2, Resource}
	}

	\initial{
		P0 = [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]]; P1 = [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]]; P2 = [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]]; Resource = [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]]; 
	}
	\transition{T0}{
		\condition{P0 > 0}
		\action{
			next->P0 = P0 - [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]];
			next->P1 = P1 + [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]];
		}
		\rate{1.0}
	}
	\transition{T1}{
		\condition{P0 > 0}
		\action{
		}
		\rate{1.0}
	}
	\transition{T2}{
		\condition{P1 > 0 && Resource > 0}
		\action{
		}
		\rate{1.0}
	}
	\transition{T3}{
		\condition{P1 > 0 && Resource > 0}
		\action{
			next->P1 = P1 - [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]];
			next->P2 = P2 + [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]];
		}
		\rate{1.0}
	}
	\transition{T4}{
		\condition{P2 > 0}
		\action{
			next->P2 = P2 - [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=], pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]];
			next->P0 = P0 + [pipe.views.MarkingView[,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=,flags=0,maximumSize=,minimumSize=,preferredSize=]];
		}
		\rate{1.0}
	}
	\transition{T5}{
		\condition{P2 > 0}
		\action{
		}
		\rate{1.0}
	}
}

\solution{
	\method{sor}

}\performance{
	\statemeasure{Enabled probability for transition T0}{
		\estimator{mean}
		\expression{(P0 > 0) ? 1 : 0}
	}
	\countmeasure{Throughput for transition T0}{
		\estimator{mean}
		\precondition{1}
		\postcondition{1}
		\transition{T0}
	}
	\statemeasure{Enabled probability for transition T1}{
		\estimator{mean}
		\expression{(P0 > 0) ? 1 : 0}
	}
	\countmeasure{Throughput for transition T1}{
		\estimator{mean}
		\precondition{1}
		\postcondition{1}
		\transition{T1}
	}
	\statemeasure{Enabled probability for transition T2}{
		\estimator{mean}
		\expression{(P1 > 0 && Resource > 0) ? 1 : 0}
	}
	\countmeasure{Throughput for transition T2}{
		\estimator{mean}
		\precondition{1}
		\postcondition{1}
		\transition{T2}
	}
	\statemeasure{Enabled probability for transition T3}{
		\estimator{mean}
		\expression{(P1 > 0 && Resource > 0) ? 1 : 0}
	}
	\countmeasure{Throughput for transition T3}{
		\estimator{mean}
		\precondition{1}
		\postcondition{1}
		\transition{T3}
	}
	\statemeasure{Enabled probability for transition T4}{
		\estimator{mean}
		\expression{(P2 > 0) ? 1 : 0}
	}
	\countmeasure{Throughput for transition T4}{
		\estimator{mean}
		\precondition{1}
		\postcondition{1}
		\transition{T4}
	}
	\statemeasure{Enabled probability for transition T5}{
		\estimator{mean}
		\expression{(P2 > 0) ? 1 : 0}
	}
	\countmeasure{Throughput for transition T5}{
		\estimator{mean}
		\precondition{1}
		\postcondition{1}
		\transition{T5}
	}
	\statemeasure{Mean tokens on place P0}{
		\estimator{mean variance distribution}
		\expression{P0}
	}
	\statemeasure{Mean tokens on place P1}{
		\estimator{mean variance distribution}
		\expression{P1}
	}
	\statemeasure{Mean tokens on place P2}{
		\estimator{mean variance distribution}
		\expression{P2}
	}
	\statemeasure{Mean tokens on place Resource}{
		\estimator{mean variance distribution}
		\expression{Resource}
	}
}
