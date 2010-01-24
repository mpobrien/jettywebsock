var numRows = 20;
var numCols = 10;
var cellSize = 20;

var pieces = 
    [<!--{{{-->
                [
                    [[0,0,0],
                     [0,1,0],
                     [1,1,1]],

                    [[1,0,0],
                     [1,1,0],
                     [1,0,0]],

                    [[1,1,1],
                     [0,1,0],
                     [0,0,0]],

                    [[0,0,1],
                     [0,1,1],
                     [0,0,1]],
                ],

                [ 
                  [[1,1],
                  [1,1]],
                ],

                [
                    [[0,1,0],
                     [0,1,0],
                     [1,1,0]],

                    [[1,0,0],
                     [1,1,1],
                     [0,0,0]],

                    [[0,1,1],
                     [0,1,0],
                     [0,1,0]],

                    [[0,0,0],
                     [1,1,1],
                     [0,0,1]],
                ],

                [
                    [[0,0,0,0],
                     [1,1,1,1],
                     [0,0,0,0],
                     [0,0,0,0]],

                    [[0,1,0,0],
                     [0,1,0,0],
                     [0,1,0,0],
                     [0,1,0,0]],
                ],

                [
                    [[0,0,0],
                     [1,1,1],
                     [1,0,0]],

                    [[1,1,0],
                     [0,1,0],
                     [0,1,0]],

                    [[0,0,1],
                     [1,1,1],
                     [0,0,0]],

                    [[0,1,0],
                     [0,1,0],
                     [0,1,1]],
                ],

                [
                    [[0,0,1],
                     [0,1,1],
                     [0,1,0]],

                    [[1,1,0],
                     [0,1,1],
                     [0,0,0]],
                ],

                [
                    [[0,1,1],
                     [1,1,0],
                     [0,0,0]],

                    [[0,1,0],
                     [0,1,1],
                     [0,0,1]],
                ]
                 
         ];<!--}}}-->

        var shape = null;
        var tetrisBoard = {
            setup: function(canvElm){<!--{{{-->
                this.canvElm = canvElm;
                this.board = [[0,0,0,0,0,0,0,0,0,0], 
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,1,0,0,0,0,0,0]];
            },<!--}}}-->
            grid: function(){<!--{{{-->
                ctx = this.canvElm.getContext("2d");
                ctx.lineWidth = 1
                ctx.beginPath()
                for( row=0; row<=numRows; row++ ){
                    ctx.moveTo(0, row*cellSize);
                    ctx.lineTo(numCols* cellSize, row*cellSize);
                    ctx.stroke()
                }
                for( col=0; col<=numCols; col++ ){
                    ctx.moveTo(col*cellSize, 0);
                    ctx.lineTo(col*cellSize, numRows*cellSize);
                    ctx.stroke()
                }
            },<!--}}}-->
            draw: function(permanent){<!--{{{-->
			    ctx = this.canvElm.getContext("2d");
                ctx.clearRect( 0, 0, cellSize*numCols, cellSize*numRows );
                this.grid()
                newboard = addmatrix( this.board, shape.matrix, shape.x, shape.y )
                for( row=0; row<numRows; row++ ){
                    for( col = 0; col < numCols; col++ ){
                        if( newboard[row][col] > 0 ){
                            ctx.beginPath();
					        ctx.fillRect( col * cellSize, row * cellSize, cellSize, cellSize)
                        }
                    }
                }
                if( permanent ){
                    this.board = newboard
                }
            },<!--}}}-->
        }

        function addmatrix(m1, m2, x, y){<!--{{{-->
            m3 = []
            for(row=0;row<m1.length;row++){ m3[row] = m1[row].slice(0) }
            for( i=0; i<m2.length; i++ ){
                for( j=0; j<m2[i].length; j++){
                    if( y + i < 0 || y + i >= numRows ) continue
                    m3[y + i][x + j] += m2[i][j]
                }
            }
            return m3
        }<!--}}}-->

        function Shape(){<!--{{{-->
            piecenum = Math.floor(Math.random()*7);
            this.pieceset = pieces[piecenum]
            this.pieceset_index = 0;
            this.numrots = this.pieceset.length;
            this.matrix = this.pieceset[ this.pieceset_index ]//init_matrix;
            this.matsize = this.matrix.length;
            var matsize = this.matsize

            this.roomtop = function(){<!--{{{-->
                var count = -1;
                for( i=0;i<matsize;i++ ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[i][j] > 0 ) return count;
                    }
                }
            }<!--}}}-->

            this.x = Math.floor( (numCols - matsize)/2 )
            //this.y = ( 0 - this.roomtop())
            this.y = 0;

            this.rotate = function(){
                //var newpieceindex = (this.pieceset_index + 1) % (this.numrots) 
                //var newmatrix = this.pieceset[newpieceindex]//init_matrix;  
                this.pieceset_index = (this.pieceset_index + 1) % (this.numrots)
                this.matrix = this.pieceset[ this.pieceset_index ]//init_matrix;
            }

            this.roomleft = function(){
                var count = -1;
                for( i=0;i<matsize;i++ ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[j][i] > 0 ) return count;
                    }
                }
                return count
            }

            this.roomright = function(){
                var count = -1;
                for( i=matsize-1;i>=0;i-- ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[j][i] > 0 ) return count;
                    }
                }
                return count
            }

            this.sideroom = function(right){
                var count = -1;
                for( i=(right?matsize-1:0);(right?i>=0:i<matsize);i+=(right?-1:1) ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[j][i] > 0 ) return count;
                    }
                }
                return count
            }

            this.clearside = function(right){
                //right = true, left = false
                var incr = right ? -1 : 1;
                var count = -1
                for( i=0;i<matsize;i++){
                    count += 1
                    for( j=right?matsize-1:0;j != (right?-1:matsize);j+=(right?-1:1)){
                        if( this.matrix[i][j] > 0 ) return count;
                    }
                }
                return count;
            }

            this.roombottom = function(){<!--{{{-->
                var count = -1;
                for( i=matsize-1;i>=0;i--){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[i][j] > 0 ) return count;
                    }
                }
            }<!--}}}-->

            this.bottoms = function(){
                var bottoms = []
                for(i=0;i<this.matsize;i++){
                    for(j=0;j<this.matsize;j++){
                        if(this.matrix[i][j] > 0 && (bottoms[j]==null || bottoms[j] < i)){
                            bottoms[j] = i
                        }
                    }
                }
                return bottoms;
            }

            this.drop = function(grid){
                var bottoms = this.bottoms();
                var maxheight = null;
                for( j=0; j<bottoms.length;j++ ){
                    if( bottoms[j] == undefined ) continue;
                    for(i=this.y+bottoms[j];i<numRows;i++){
                        if( grid[i][this.x + j] > 0 ){
                            var maxheightTest = i + (this.matsize-bottoms[j])-this.matsize-1
                            if( maxheight == null || maxheight> maxheightTest ){
                                maxheight = maxheightTest
                                break;
                            }
                        }
                    }
                }
                if(maxheight==null) maxheight=numRows-this.matsize+this.roombottom()
                return maxheight
            }


        }<!--}}}-->

        function randompiece(){ return pieces[Math.floor(Math.random()*7)][0] }

