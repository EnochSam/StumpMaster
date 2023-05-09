<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>${username}'s Game </title>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css">
</head>
<body>
    <form method="post" id= "gameForm" action="${pageContext.servletContext.contextPath}/Game">
        	<input type = "hidden" value ="${username}" name=username >
    
    <div id="Board">
        
    </div>
    <input type="submit" onclick = sendPost() name="Save" value="Save Game">
    	<input type = "hidden" value ="${username}" name=username >
    </form>
    <form id ="form" method="get" action="${pageContext.servletContext.contextPath}/MainMenu">
    	<input type="submit" value="Quit Game">
    	<input type = "hidden" value ="${username}" name=username >
    
    </form>
    <script>
    	let pieceImg = ${boardJson};
    
    	//Grabs Values passed from Servelt and store them in variables
    	let possibleMoves = "${possibleMoves}"
    	let RightCaptured = ${rightCaptured};
    	let LeftCaptured = ${leftCaptured};
    	let playerTurn = "${playerTurn}";
		let selectingPiece = "${selectingPiece}"
		let selectValidMoves = "${selectValidMoves}"
		let inCheck = "${inCheck}"
		let inCheckmate = "${inCheckmate}"
		let tileStart = ${beginningOfGame};
		let pawnPromotion = "${PawnPromotion}";
		let gameMoves = "${gameMoves}"
		let mateinone = "${mateinone}"
		let tile = "${tile}"
   
            //Creates Tile Submission
            let submit = document.createElement("input")
            submit.name = "tile"
            submit.value = document.activeElement.name
            submit.type = "hidden"
           
            let Sessionsubmit = document.createElement("input")
            Sessionsubmit.name = "gameMoves"
            Sessionsubmit.value = gameMoves
            Sessionsubmit.type = "hidden"
            
            //Create selectingPiece
            let selectValidMovesSubmission = document.createElement("input")
            selectValidMovesSubmission.name = "selectValidMoves"
            selectValidMovesSubmission.value = selectValidMoves
            selectValidMovesSubmission.type = "hidden"
            
            //Create selectValidMoves
            let selectingPieceSubmission = document.createElement("input")
            selectingPieceSubmission.name = "selectingPiece"
            selectingPieceSubmission.value = selectingPiece
            selectingPieceSubmission.type = "hidden"
            console.log(submit.value)
            
            //Create LastMove Submit
            let LastMovesubmit = document.createElement("input")
            LastMovesubmit.name = "lastMove"
            LastMovesubmit.value = sessionStorage.getItem("lastMove")
            LastMovesubmit.type = "hidden"
            
            //Create PlayerTurn Submit
            let PlayerMovesubmit = document.createElement("input")
            PlayerMovesubmit.name = "playerTurn"
            PlayerMovesubmit.value = playerTurn
            PlayerMovesubmit.type = "hidden"
            
            let tileSubmit = document.createElement("input")
            tileSubmit.name = "tile"
            tileSubmit.value = tile
            tileSubmit.type = "hidden"
            
            //Add Submissions to submit
            document.getElementById("Board").appendChild(submit)
            document.getElementById("Board").appendChild(Sessionsubmit)
            document.getElementById("Board").appendChild(selectingPieceSubmission)
            document.getElementById("Board").appendChild(selectValidMovesSubmission)
            document.getElementById("Board").appendChild(LastMovesubmit)
            document.getElementById("Board").appendChild(PlayerMovesubmit)
            document.getElementById("Board").appendChild(tileSubmit)

document.getElementById("gameForm").submit()
        
        
    </script>
</body>
</html>