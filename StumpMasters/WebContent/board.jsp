<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Enoch a Bigger Poopie</title>
</head>
<body>
    <form method="post" action="Game">
    <div id="Board">
        
    </div>
    </form>
    <form method="get" action="MainMenu">
    	<input type="submit" value="Quit Game">
    </form>
    <script>
        //Creates board using javascript tags

            //Create table
             //<td> == column
             //<tr> == row
            let board = document.createElement("table");
            board.style =
            "display : grid;"+
            "justify-content: space-around;"+
            "border : 5px"+
            "row-gap : 0px";
            for(let y=8; y >= 1; y--){
                let tr = document.createElement("tr")
                tr.style = "display :flex;";
                let td = null;
                let point = null;
                for(let  x= 1; x <= 8; x++){
                    
                    //create new space in x axis
                    td = document.createElement("td");
                    td.oncklick = "submit"
                    td.style = "padding : 0px;"
                    +"display : contents;"
                    point = document.createElement("input")
                    point.type = "button"
                    point.value = ""
                    point.name = x+":"+y;
                    if((x+y) %2 == 0){
                        //Black Spaces
                        color = "brown"
                    }else{
                        //White Spaces
                        color = "green"
                    }

                    point.style = 

                    "border : 0px solid;"+
                    "padding : 0px;"+
                    "width : 50px;"+
                    "height : 50px;"+
                    //"float : left;"+
                    "background-color :"+color+";";
                    
                    
                    point.onclick = sendPost
                    point.type = "submit"
                    
                   
                    td.appendChild(point)
                    tr.appendChild(td)
                }

                board.appendChild(tr)

            }
            document.getElementById("Board").appendChild(board)

        function sendPost(){
            /*
            let tile = document.activeElement
            tile.value = tile.name
            tile.name = "tile"
            console.log("Submitted name", tile.name)
            console.log("Submitted value", tile.value)
            */
            let submit = document.createElement("input")
            submit.name = "tile"
            submit.value = document.activeElement.name
            submit.type = "hidden"
            document.getElementById("Board").appendChild(submit)
            
            
        }
    </script>
</body>
</html>