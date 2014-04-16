package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.GraphUtils;
import io.mem0r1es.activitysubsumer.wordnet.WordNetGraphs;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.testng.annotations.Test;

/**
 * Helper class used to identify which nodes we want to use as roots for the sub-graphs, when they
 * are going to be built.
 * 
 * @author horiaradu
 */
@Test
public class GraphDepthPrecomputer {
	@Test
	public void testJavaSerialization() throws Exception {
		WordNetGraphs.initialize(new FileInputStream(Cons.NOUNS_GRAPH), new FileInputStream(Cons.VERBS_GRAPH));
		Map<String, Integer> subtreeHeights = new HashMap<String, Integer>();

		DirectedAcyclicGraph<String, DefaultEdge> nouns = WordNetGraphs.instance().getNouns();
		/**
		 * DEFAULT_ROOT<br>
		 * - entity.n.01<br>
		 * -- abstraction.n.06 -> 16<br>
		 * --- attribute.n.02 -> 14<br>
		 * ----- shape.n.02=9<br>
		 * ----- space.n.01=4<br>
		 * ----- common_denominator.n.02=1<br>
		 * ----- personality.n.01=3<br>
		 * ----- property.n.02=8<br>
		 * ----- ballast.n.03=1<br>
		 * ----- cheerfulness.n.01=2<br>
		 * ----- inheritance.n.04=3<br>
		 * ----- character.n.09=2<br>
		 * ----- depth.n.06=1<br>
		 * ----- state.n.02=13<br>
		 * ----- eidos.n.01=1<br>
		 * ----- time.n.05=5<br>
		 * ----- trait.n.01=7<br>
		 * ----- uncheerfulness.n.02=2<br>
		 * ----- quality.n.01=13<br>
		 * ----- thing.n.09=1<br>
		 * ----- ethos.n.01=1<br>
		 * ----- human_nature.n.01=1 <br>
		 * --- measure.n.02 -> 10<br>
		 * --- communication.n.02 -> 10<br>
		 * --- relation.n.01 -> 15<br>
		 * --- group.n.01 -> 13<br>
		 * --- set.n.02 -> 4<br>
		 * --- otherworld.n.01 -> 1<br>
		 * --- psychological_feature.n.01 -> 14<br>
		 * -- thing.n.08 -> 2<br>
		 * --- freshener.n.01 -> 1<br>
		 * --- security_blanket.n.01 -> 1<br>
		 * --- whacker.n.01 -> 1<br>
		 * --- jimdandy.n.02 -> 1<br>
		 * --- horror.n.02 -> 1<br>
		 * --- pacifier.n.02 -> 1<br>
		 * --- stinker.n.02 -> 1<br>
		 * --- change.n.06 -> 1<br>
		 * -- physical_entity.n.01 -> 19<br>
		 * --- thing.n.12 -> 11<br>
		 * ---- reservoir.n.04=1<br>
		 * ---- unit.n.05=10<br>
		 * ---- inessential.n.01=3<br>
		 * ---- necessity.n.02=2<br>
		 * ---- variable.n.01=1<br>
		 * ---- subject.n.02=1<br>
		 * ---- part.n.03=10<br>
		 * ---- body_of_water.n.01=5<br>
		 * --- causal_agent.n.01 -> 11<br>
		 * ---- {nature.n.02=1, destiny.n.02=1, catalyst.n.02=1, deus_ex_machina.n.01=1,
		 * operator.n.02=4, vital_principle.n.01=5, agent.n.01=7, engine.n.02=1, agent.n.03=9,
		 * cause_of_death.n.01=1, supernatural.n.01=1, power.n.05=2, person.n.01=10,
		 * first_cause.n.01=1, theurgy.n.01=1, danger.n.03=4}<br>
		 * --- object.n.01 -> 18<br>
		 * ---- {moon.n.02=1, shiner.n.02=2, film.n.04=4, triviality.n.03=2, web.n.01=3,
		 * catch.n.04=1, remains.n.01=3, land.n.04=5, commemorative.n.01=1, land.n.02=4,
		 * snake.n.05=2, hoodoo.n.04=1, hail.n.02=1, draw.n.04=1, whole.n.02=17, fomite.n.01=1,
		 * keepsake.n.01=3, paring.n.02=1, geological_formation.n.01=7, ribbon.n.01=2,
		 * makeweight.n.01=1, growth.n.07=2, property.n.05=2, head.n.17=1, part.n.02=7,
		 * charm.n.03=3, stuff.n.02=2, wall.n.02=2, vagabond.n.01=1, neighbor.n.02=1,
		 * je_ne_sais_quoi.n.01=1, discard.n.01=1, curio.n.01=3, ice.n.02=2, finding.n.03=1,
		 * location.n.01=9, floater.n.07=3}<br>
		 * --- matter.n.03 -> 14<br>
		 * --- substance.n.04 -> 1<br>
		 * --- process.n.06 -> 12
		 */
		String root = "process.n.06";
		Set<DefaultEdge> edgesOfRoot = nouns.edgesOf(root);
		for (DefaultEdge edgeOfRoot : edgesOfRoot) {
			String edgeSource = nouns.getEdgeSource(edgeOfRoot);
			String edgeTarget = nouns.getEdgeTarget(edgeOfRoot);
			if (edgeSource.equals(root)) {
				int maxHeight = 0;

				PathBuilder<String, DefaultEdge> pathBuilder = new PathBuilder<String, DefaultEdge>(nouns, edgeTarget, nouns.vertexSet());
				for (Set<List<String>> setOfPaths : pathBuilder.getAllPaths().values()) {
					for (List<String> path : setOfPaths) {
						if (path.size() > maxHeight) {
							maxHeight = path.size();
						}
					}
				}

				subtreeHeights.put(edgeTarget, maxHeight);
			}
		}

		System.out.println(subtreeHeights);

		DirectedAcyclicGraph<String, DefaultEdge> verbs = WordNetGraphs.instance().getVerbs();
		subtreeHeights = new HashMap<String, Integer>();
		/**
		 * DEFAULT_ROOT<br>
		 * win.v.01=2, desire.v.01=4, be.v.05=1, air.v.02=2, be.v.08=2, become.v.03=4,
		 * appoint.v.02=6, oversleep.v.01=1, rest.v.11=2, dissemble.v.03=3, be.v.02=1, be.v.01=6,
		 * madden.v.01=2, be.v.03=6, issue.v.04=2, originate_in.v.01=2, storm.v.03=1,
		 * stay_in_place.v.01=2, go.v.28=1, splash.v.07=2, precede.v.03=1, err.v.01=4,
		 * differ.v.01=4, settle.v.21=2, greet.v.04=1, prevent.v.01=4, be.v.11=1, bring_on.v.03=1,
		 * be_full.v.01=1, say.v.09=1, percolate.v.04=2, need.v.03=1, rest.v.03=2, break.v.44=1,
		 * protuberate.v.01=1, reach_one's_nostrils.v.01=1, prevent.v.02=5, absorb.v.04=3,
		 * absorb.v.06=2, break.v.46=2, bring.v.06=1, break_even.v.01=1, get_down.v.07=3,
		 * ignore.v.05=1, play.v.11=1, keep.v.01=4, ignore.v.03=2, issue_forth.v.01=1, check.v.08=1,
		 * play.v.18=1, induce.v.02=5, unbalance.v.01=1, happen.v.01=3, wait.v.01=2,
		 * apologize.v.01=1, grudge.v.02=1, survive.v.01=2, down.v.04=1, settle.v.04=2, play.v.26=2,
		 * bring_in.v.01=2, play.v.22=1, enjoy.v.01=2, record.v.04=1, function.v.01=2,
		 * belong_to.v.01=2, fetch.v.02=1, mingle.v.02=1, come_near.v.01=1, begrudge.v.02=1,
		 * end.v.01=3, overlay.v.01=2, cling.v.02=1, miss.v.08=1, miss.v.06=2, miss.v.07=2,
		 * yield.v.05=2, miss.v.01=2, equal.v.01=7, leave.v.12=2, cut.v.04=1, violate.v.01=2,
		 * mire.v.02=1, free.v.07=3, free.v.06=3, predominate.v.01=2, attract.v.02=3,
		 * rule_out.v.03=1, repent.v.01=1, nod.v.03=1, confront.v.03=1, photograph.v.02=1,
		 * take_the_floor.v.02=1, stampede.v.02=1, stampede.v.01=1, detonate.v.02=1,
		 * transfer.v.05=8, leave.v.01=5, leave.v.02=3, abstain.v.02=3, intend.v.01=5, think.v.12=1,
		 * snatch.v.02=1, call.v.16=1, assail.v.01=3, retire.v.10=1, disappear.v.01=2, spell.v.03=2,
		 * separate.v.01=1, start.v.10=4, separate.v.07=4, separate.v.08=2, accommodate.v.04=2,
		 * affect.v.05=5, spread.v.10=1, remember.v.02=5, save.v.04=2, remember.v.01=5, save.v.03=3,
		 * save.v.05=2, remember.v.03=2, dislike.v.01=4, exempt.v.01=2, exist.v.01=6, exist.v.02=2,
		 * snuff_out.v.02=2, free.v.01=2, desegregate.v.01=1, notice.v.02=2, suction.v.01=1,
		 * start.v.08=2, predate.v.01=1, start.v.09=2, spread.v.01=5, yield.v.12=3, overarch.v.02=1,
		 * break.v.25=1, identify.v.05=1, sit.v.01=3, exhaust.v.05=5, give_up.v.11=1,
		 * address.v.02=2, embitter.v.01=1, collapse.v.05=2, satisfy.v.02=3, kill.v.09=2,
		 * despair.v.01=2, derive.v.05=1, get_it.v.02=1, receive.v.05=2, admit.v.06=1,
		 * choose.v.02=2, admit.v.05=1, hire.v.01=3, kill.v.01=4, control.v.01=6, trade.v.01=4,
		 * determine.v.03=6, include.v.01=5, feel.v.09=1, determine.v.01=3, commemorate.v.02=1,
		 * determine.v.08=2, welter.v.03=1, feel.v.01=6, kill.v.10=2, metabolize.v.01=1,
		 * sniff_out.v.01=1, send_in.v.02=1, consume.v.02=5, persist.v.03=2, expect.v.03=3,
		 * sympathize.v.02=1, compete.v.01=6, account.v.02=5, travel.v.01=6, travel.v.03=5,
		 * perceive.v.01=4, port.v.02=1, fall_asleep.v.01=2, designate.v.01=5, dismiss.v.02=1,
		 * close_up.v.04=1, complete.v.05=1, own.v.01=2, fruit.v.01=1, sulk.v.01=2, close_up.v.01=3,
		 * concern.v.02=1, constitute.v.01=3, take_to.v.02=1, trip.v.02=1, trip.v.05=1, leak.v.04=2,
		 * connect.v.01=6, produce.v.02=6, connect.v.03=4, lose_sight_of.v.01=1, waive.v.01=2,
		 * campaign.v.03=2, lead.v.08=1, cheer.v.03=3, produce.v.06=2, arrive.v.01=4, join.v.03=1,
		 * unmake.v.01=6, join.v.01=4, consider.v.04=1, lead.v.01=3, wake.v.01=2, beat.v.16=2,
		 * keep_up.v.05=1, keep_up.v.04=1, keep_up.v.01=2, use.v.03=3, insist.v.01=5,
		 * get_off.v.02=1, use.v.01=5, get_off.v.04=1, range.v.03=2, clear.v.07=1, go_to_bed.v.01=3,
		 * study.v.02=2, get.v.20=1, prod.v.02=3, mean.v.03=5, mean.v.05=1, gather.v.01=4,
		 * last_out.v.01=2, hide.v.01=4, owe.v.01=2, hide.v.02=2, create.v.02=3, give.v.08=4,
		 * vote.v.04=1, remove.v.01=5, remove.v.02=5, bethink.v.01=1, film_over.v.01=1, get.v.25=1,
		 * assemble.v.03=2, arraign.v.01=1, give.v.01=3, remove.v.08=2, excel.v.01=2, lie.v.02=6,
		 * discourage.v.02=3, attach_to.v.01=2, worry.v.01=3, lend_oneself.v.01=1, back.v.04=1,
		 * hold.v.02=5, flow.v.03=1, degrade.v.01=1, go_into.v.02=1, support.v.01=7, gain.v.05=4,
		 * take_orders.v.02=1, rise.v.12=1, examine.v.02=3, out.v.03=1, submit.v.03=2, click.v.07=1,
		 * gag.v.07=1, hold.v.10=3, stay.v.05=1, beat.v.02=3, trap.v.04=1, premier.v.01=1,
		 * displease.v.01=6, oppress.v.01=2, guarantee.v.02=2, decertify.v.01=1, hold.v.14=4,
		 * grant.v.05=1, thrust.v.06=1, stag.v.01=1, disagree.v.01=5, discover.v.07=2, get.v.01=5,
		 * deafen.v.01=1, treat.v.03=5, itch.v.03=1, disarm.v.01=1, freeze.v.06=1, shine.v.02=3,
		 * lie_in.v.02=1, trail.v.04=1, install.v.02=3, install.v.03=1, work.v.01=3, work.v.02=4,
		 * hold_one's_own.v.01=1, burn.v.05=4, blind.v.01=2, rear.v.02=2, have.v.01=8, agree.v.07=3,
		 * thunder.v.03=1, abandon.v.04=1, inherit.v.02=1, watch.v.04=2, watch.v.01=4, emit.v.02=3,
		 * jump.v.11=1, breathe.v.01=4, abandon.v.02=3, watch.v.05=2, gladden.v.01=2, watch.v.06=1,
		 * retreat.v.04=1, refrain.v.01=3, traverse.v.03=1, change.v.02=8, change.v.03=4,
		 * amount.v.01=2, search.v.01=3, necessitate.v.01=2, overload.v.01=1, shine.v.07=1,
		 * change.v.01=8, stand_still.v.01=3, note.v.03=1, have.v.10=1, enter.v.01=6, go_by.v.03=1,
		 * carry_to_term.v.01=1, keep_to_oneself.v.01=1, avoid.v.01=3, pronounce.v.01=3,
		 * collide.v.02=3, avoid.v.03=3, open.v.08=1, enter.v.02=2, have.v.02=4, check_out.v.05=1,
		 * rage.v.03=2, succeed.v.01=4, enter.v.06=1, let.v.01=2, have.v.09=2, succeed.v.02=4,
		 * stop.v.01=2, remind.v.01=3, entrench.v.03=1, cause_to_be_perceived.v.01=5,
		 * neglect.v.04=2, overtake.v.01=1, prosecute.v.02=1, uncover.v.02=2, be_active.v.01=2,
		 * offer.v.07=2, stop.v.05=3, cover.v.01=4, depend_on.v.01=3, depend_on.v.02=1,
		 * depend_on.v.03=1, interpose.v.01=1, open.v.01=2, open.v.02=2, confine.v.05=2,
		 * neglect.v.03=2, affirm.v.03=6, neglect.v.01=2, rid.v.01=3, practice.v.01=2,
		 * indulge.v.01=2, practice.v.04=2, die.v.11=1, appear.v.07=1, woo.v.01=1, know.v.07=2,
		 * switch.v.03=3, pass.v.22=3, fear.v.02=3, appear.v.02=3, fear.v.04=1, tithe.v.03=1,
		 * bristle.v.02=1, appear.v.05=3, keep_quiet.v.01=1, understand.v.01=4, understand.v.02=3,
		 * arm.v.01=2, establish.v.08=2, live.v.07=1, take_the_stage.v.01=1, fall_short_of.v.01=1,
		 * let_go_of.v.01=3, straddle.v.03=1, delight.v.02=2, fly.v.05=2, postdate.v.01=1,
		 * conform.v.01=2, visit.v.04=1, sensitize.v.02=4, refer.v.02=4, take_out.v.01=2,
		 * plunge.v.05=1, reject.v.06=1, fall.v.14=1, blow.v.02=2, perform.v.01=4, agree.v.01=7,
		 * live.v.02=2, put_away.v.04=1, observe.v.08=1, observe.v.09=2, get_rid_of.v.01=5,
		 * observe.v.06=2, surveil.v.01=1, resonate.v.02=2, agree.v.05=1, learn.v.01=4, fall.v.18=1,
		 * leave_behind.v.01=2, keep_down.v.03=1, fall.v.17=2, conceal.v.02=2, learn.v.04=3,
		 * learn.v.02=3, like.v.03=1, wear.v.01=1, reflect.v.03=2, like.v.02=3, experiment.v.02=1,
		 * punish.v.01=4, fracture.v.05=2, know.v.01=3, know.v.02=3, know.v.03=2, know.v.04=1,
		 * converge.v.01=2, guide.v.05=4, defend.v.06=1, hit.v.09=1, charge.v.02=4, roll.v.03=1,
		 * diverge.v.03=2, confirm.v.01=5, lose.v.01=2, lose.v.02=2, lose.v.05=2,
		 * care_a_hang.v.01=1, lose.v.06=1, lose.v.07=1, lose.v.08=1, find.v.01=1, begin.v.02=2,
		 * express.v.02=8, hang.v.08=1, wreathe.v.01=1, hang.v.05=1, hang.v.01=3, decide.v.01=5,
		 * make.v.03=7, drive_in.v.01=1, make.v.01=3, express_emotion.v.01=3, cancel.v.01=1,
		 * get_up.v.02=1, move.v.15=3, begin.v.10=1, begin.v.03=4, exceed.v.01=2, begin.v.05=1,
		 * hesitate.v.01=3, exercise.v.04=3, afford.v.04=1, afford.v.03=1, contend.v.06=5,
		 * show.v.04=4, deliver.v.08=1, fail.v.09=1, fail.v.08=1, fail.v.05=1, fail.v.07=1,
		 * fail.v.01=4, fail.v.02=2, spend.v.01=3, catch_it.v.01=1, look.v.01=3, outlive.v.01=1,
		 * disorganize.v.01=1, cultivate.v.01=5, think.v.03=9, close.v.01=3, sound.v.03=2,
		 * drive.v.12=1, sound.v.06=3, fail.v.10=1, ascend.v.04=1, abolish.v.01=2,
		 * get_the_better_of.v.01=5, take.v.39=1, destroy.v.02=3, set_in.v.03=1, take.v.34=1,
		 * go.v.16=1, correspond.v.03=2, levitate.v.02=1, trigger.v.02=1, reach.v.01=3, go.v.23=1,
		 * go.v.22=1, catch.v.16=1, do.v.11=1, dishonor.v.01=2, complain.v.01=3, meet.v.08=1,
		 * disrupt.v.02=1, meet.v.02=4, drop_out.v.01=1, utter.v.02=5, meet.v.01=4, obviate.v.01=2,
		 * go.v.05=1, love.v.01=4, precipitate.v.03=4, forget.v.01=9, forget.v.02=2, analyze.v.01=5,
		 * analyze.v.03=2, analyze.v.02=2, meet.v.10=3, stick_out.v.01=3, turn_to.v.02=1,
		 * bore.v.01=1, move.v.04=4, move.v.03=6, taste.v.01=2, lie_dormant.v.01=1,
		 * discontinue.v.01=3, move.v.02=13, go_down.v.04=1, go_down.v.05=1, clear_up.v.04=1,
		 * refuse.v.02=2, fall_in.v.02=1, idle.v.01=1, idle.v.02=2, take.v.20=2, take.v.21=5,
		 * take.v.04=6, comfort.v.01=3, act.v.08=3, take.v.08=5, take_a_dare.v.01=1, act.v.01=11,
		 * act.v.02=4, hang.v.10=1, hang.v.13=1, spot.v.02=2, touch.v.05=7, fire.v.05=1,
		 * follow.v.10=1, fire.v.02=4, touch.v.01=8, season.v.01=3
		 */
		root = GraphUtils.DEFAULT_ROOT;
		edgesOfRoot = verbs.edgesOf(root);
		for (DefaultEdge edgeOfRoot : edgesOfRoot) {
			String edgeSource = verbs.getEdgeSource(edgeOfRoot);
			String edgeTarget = verbs.getEdgeTarget(edgeOfRoot);
			if (edgeSource.equals(root)) {
				int maxHeight = 0;

				PathBuilder<String, DefaultEdge> pathBuilder = new PathBuilder<String, DefaultEdge>(verbs, edgeTarget, verbs.vertexSet());
				for (Set<List<String>> setOfPaths : pathBuilder.getAllPaths().values()) {
					for (List<String> path : setOfPaths) {
						if (path.size() > maxHeight) {
							maxHeight = path.size();
						}
					}
				}

				subtreeHeights.put(edgeTarget, maxHeight);
			}
		}

		System.out.println(subtreeHeights);
	}
}
