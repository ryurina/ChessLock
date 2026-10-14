package app.chesslock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.chesslock.ui.*
import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.move.Move
import kotlinx.coroutines.delay

class PuzzleState(val puzzle: Puzzle) {
    val board = Board().apply { loadFromFen(puzzle.fen) }
    var tick by mutableIntStateOf(0)
    var selected by mutableStateOf<Square?>(null)
    var last by mutableStateOf<Pair<Square, Square>?>(null)
    var msg by mutableStateOf("Find the best move.")
    var solved by mutableStateOf(false)
    var failed by mutableStateOf(false)
    var whiteView = true
    private var idx = 1

    init { play(puzzle.moves[0]); whiteView = board.sideToMove == Side.WHITE }

    private fun play(uci: String) {
        val m = Move(uci, board.sideToMove)
        board.doMove(m); last = m.from to m.to; tick++
    }

    fun targets(): Set<Square> {
        val s = selected ?: return emptySet()
        return board.legalMoves().filter { it.from == s }.map { it.to }.toSet()
    }

    fun tap(sq: Square) {
        if (solved || failed) return
        val sel = selected
        val p = board.getPiece(sq)
        val own = p != Piece.NONE && p.pieceSide == board.sideToMove
        if (sel == null || own) { selected = if (own) sq else null; return }
        val cand = board.legalMoves().filter { it.from == sel && it.to == sq }
        selected = null
        if (cand.isEmpty()) return
        val mv = cand.firstOrNull { it.promotion == Piece.NONE || it.promotion.pieceType == PieceType.QUEEN } ?: cand.first()
        val exp = Move(puzzle.moves[idx], board.sideToMove)
        board.doMove(mv); last = mv.from to mv.to; tick++
        if (!((mv.from == exp.from && mv.to == exp.to) || board.isMated)) {
            failed = true; msg = "Wrong move. Loading a new puzzle."; return
        }
        idx++
        if (idx >= puzzle.moves.size || board.isMated) { solved = true; msg = "Solved. Opening your app."; return }
        play(puzzle.moves[idx]); idx++
        msg = "Correct. Keep going."
    }
}

class PuzzleActivity : ComponentActivity() {
    private var target: String? = null

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Puzzles.load(this)
        target = intent.getStringExtra("target")
        val label = target?.let { runCatching { packageManager.getApplicationLabel(packageManager.getApplicationInfo(it, 0)).toString() }.getOrNull() }
        setContent { BTheme {
            BackHandler { goHome() }
            var st by remember { mutableStateOf(PuzzleState(Puzzles.random())) }
            LaunchedEffect(st.solved) {
                if (st.solved) {
                    delay(700)
                    target?.let { t ->
                        Prefs.grant(this@PuzzleActivity, t)
                        packageManager.getLaunchIntentForPackage(t)?.let { startActivity(it) }
                    }
                    finish()
                }
            }
            LaunchedEffect(st.failed) {
                if (st.failed) { delay(1400); st = PuzzleState(Puzzles.random(st.puzzle)) }
            }
            Column(
                Modifier.systemBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(Modifier.fillMaxWidth().brutal(B.Yellow).padding(14.dp)) {
                    BText(if (label != null) "$label is locked. Solve to open it." else "Practice puzzle", 22)
                }
                Box(Modifier.fillMaxWidth().brutal(if (st.failed) B.Red else if (st.solved) B.Green else B.White, 4.dp).padding(12.dp)) {
                    BText("${if (st.whiteView) "White" else "Black"} to move. ${st.msg}", 16)
                }
                BoardView(st.board, st.tick, st.whiteView, st.selected, st.targets(), st.last) { st.tap(it) }
                Spacer(Modifier.height(4.dp))
                BButton("Give up and go home", Modifier.fillMaxWidth(), B.Pink) { goHome() }
            }
        } }
    }
}
