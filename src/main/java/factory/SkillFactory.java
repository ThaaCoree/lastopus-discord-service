package main.factory;

import model.entity.skills.list.item.defensive.*;
import model.entity.skills.list.item.offensive.*;
import model.entity.skills.list.item.support.*;
import model.entity.skills.list.item.utility.*;
import model.entity.skills.list.monster.*;
import model.entity.skills.list.npc.*;
import model.entity.skills.list.player.*;
import model.entity.units.Unit;
import model.entity.skills.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SkillFactory {
    private static final Map<String, Supplier<Skill>> skillMap = new HashMap<>();
    public static List<String> skillNames;

    static {
        skillMap.put(Absolute_Negation.NAME, Absolute_Negation::new);
        skillMap.put(Auto_Haze.NAME, Auto_Haze::new);
        skillMap.put(Azure_Smite.NAME, Azure_Smite::new);
        skillMap.put(Be_One.NAME, Be_One::new);
        skillMap.put(Behind.NAME, Behind::new);
        skillMap.put(Black_Scythe_Comet.NAME, Black_Scythe_Comet::new);
        skillMap.put(Blue_Comet.NAME, Blue_Comet::new);
        skillMap.put(Celestial_Moonlit_Dance.NAME, Celestial_Moonlit_Dance::new);
        skillMap.put(Connect.NAME, Connect::new);
        skillMap.put(Divergent.NAME, Divergent::new);
        skillMap.put(Fateful_Impact.NAME, Fateful_Impact::new);
        skillMap.put(Fatima.NAME, Fatima::new);
        skillMap.put(Good_Bye.NAME, Good_Bye::new);
        skillMap.put(Ground_Slide.NAME, Ground_Slide::new);
        skillMap.put(Heavy_Punch.NAME, Heavy_Punch::new);
        skillMap.put(Imagetion.NAME, Imagetion::new);
        skillMap.put(Imagetion_Arrow.NAME, Imagetion_Arrow::new);
        skillMap.put(Kyrraneth.NAME, Kyrraneth::new);
        skillMap.put(Last_Hope.NAME, Last_Hope::new);
        skillMap.put(Moonlight_Sonata.NAME, Moonlight_Sonata::new);
        skillMap.put(Mythic_Gleaming.NAME, Mythic_Gleaming::new);
        skillMap.put(New_Moon_Lunar.NAME, New_Moon_Lunar::new);
        skillMap.put(Open_The_Pages.NAME, Open_The_Pages::new);
        skillMap.put(Overcharge.NAME, Overcharge::new);
        skillMap.put(Rite_of_the_Pale_Moon.NAME, Rite_of_the_Pale_Moon::new);
        skillMap.put(re_by_y.NAME, re_by_y::new);
        skillMap.put(Rule_Of_Rogue.NAME, Rule_Of_Rogue::new);
        skillMap.put(Sandstorm.NAME, Sandstorm::new);
        skillMap.put(Shadow_Field.NAME, Shadow_Field::new);
        skillMap.put(Shrunken_Knowledges.NAME, Shrunken_Knowledges::new);
        skillMap.put(Thunderbolt.NAME, Thunderbolt::new);
        skillMap.put(Twilight_Seafloor.NAME, Twilight_Seafloor::new);
        skillMap.put(VaalGaze_of_Aerithra.NAME, VaalGaze_of_Aerithra::new); //to-do
        skillMap.put(Zelvalnis.NAME, Zelvalnis::new);
        skillMap.put(ZephyrEko.NAME, ZephyrEko::new);
        skillMap.put(Tougher.NAME, Tougher::new);
        skillMap.put(Piercing_Nails.NAME, Piercing_Nails::new);
        skillMap.put(Chomp_Chomp.NAME, Chomp_Chomp::new);
        skillMap.put(Stalker_Pounce.NAME, Stalker_Pounce::new);
        skillMap.put(Ground_Stomp.NAME, Ground_Stomp::new);
        skillMap.put(Clap_Smash.NAME, Clap_Smash::new);
        skillMap.put(Twin_Twice.NAME, Twin_Twice::new);
        skillMap.put(Hand_Smash.NAME, Hand_Smash::new);
        skillMap.put(KM_L_Eyebeam.NAME, KM_L_Eyebeam::new);
        skillMap.put(KM_R_Cannon.NAME, KM_R_Cannon::new);
        skillMap.put(Foreseen.NAME, Foreseen::new);
        skillMap.put(Seen_Strike.NAME, Seen_Strike::new);
        skillMap.put(Destiny_Avoidance.NAME, Destiny_Avoidance::new);
        skillMap.put(One_Step_Ahead.NAME, One_Step_Ahead::new);
        skillMap.put(Sand_Toss.NAME, Sand_Toss::new);
        skillMap.put(Sand_Dive.NAME, Sand_Dive::new);
        skillMap.put(Sand_Drill.NAME, Sand_Drill::new);
        skillMap.put(Neriramus.NAME, Neriramus::new);
        skillMap.put(Lux_Divina.NAME, Lux_Divina::new);
        skillMap.put(Refentio.NAME, Refentio::new);
        skillMap.put(Refitia.NAME, Refitia::new);
        skillMap.put(Ocean_Surface_Reflection.NAME, Ocean_Surface_Reflection::new);
        skillMap.put(Daybreak.NAME, Daybreak::new);
        skillMap.put(Thorn_Bullet.NAME, Thorn_Bullet::new);
        skillMap.put(Thorn_Vines.NAME, Thorn_Vines::new);
        skillMap.put(Debuffer.NAME, Debuffer::new);
        skillMap.put(Bearers_Fate.NAME, Bearers_Fate::new);
        skillMap.put(Fox_Fire.NAME, Fox_Fire::new);
        skillMap.put(Power_Shot.NAME, Power_Shot::new); //to-do
        skillMap.put(Destructive_Flare.NAME, Destructive_Flare::new);
        skillMap.put(Quick_Shot.NAME, Quick_Shot::new);
        skillMap.put(Reality_Overridden.NAME, Reality_Overridden::new);
        skillMap.put(Time_Reclaimed.NAME, Time_Reclaimed::new);
        skillMap.put(Sanity_Switch.NAME, Sanity_Switch::new);
        skillMap.put(Parabellum.NAME, Parabellum::new);
        skillMap.put(The_Dawn.NAME, The_Dawn::new);
        skillMap.put(Orbital_Strike.NAME, Orbital_Strike::new);
        skillMap.put(Sorcery_Focus.NAME, Sorcery_Focus::new);
        skillMap.put(Nuclear_Fission.NAME, Nuclear_Fission::new);
        skillMap.put(Disintegrating_Ray.NAME, Disintegrating_Ray::new);
        skillMap.put(Paper_Fortress.NAME, Paper_Fortress::new);
        skillMap.put(Inscribed_Shield.NAME, Inscribed_Shield::new);
        skillMap.put(Pounce.NAME, Pounce::new);
        skillMap.put(Striking_Light.NAME, Striking_Light::new);
        skillMap.put(Inscribed_Illusory.NAME, Inscribed_Illusory::new);
        skillMap.put(Elemental_Strike.NAME, Elemental_Strike::new);
        skillMap.put(Hands_Cripple.NAME, Hands_Cripple::new);
        skillMap.put(Radiance.NAME, Radiance::new);
        skillMap.put(Twilight.NAME, Twilight::new);
        skillMap.put(Farshot.NAME, Farshot::new);
        skillMap.put(Singularity_Collapse.NAME, Singularity_Collapse::new);
        skillMap.put(Star_Wrath.NAME, Star_Wrath::new);
        skillMap.put(Shadow_Form.NAME, Shadow_Form::new);
        skillMap.put(All_Bloom.NAME, All_Bloom::new);
        skillMap.put(Star_Force.NAME, Star_Force::new);
        skillMap.put(Starfall.NAME, Starfall::new); //unfinished
        skillMap.put(Constellation_Seal.NAME, Constellation_Seal::new);
        skillMap.put(Star_Align.NAME, Star_Align::new);
        skillMap.put(Hunger_Drain.NAME, Hunger_Drain::new);
        skillMap.put(Arcane_Bolt.NAME, Arcane_Bolt::new);
        skillMap.put(Arcane_Surge.NAME, Arcane_Surge::new);
        skillMap.put(Glyph_of_Flame.NAME, Glyph_of_Flame::new);
        skillMap.put(Totem_of_Motion.NAME, Totem_of_Motion::new);
        skillMap.put(Shifting_Slab.NAME, Shifting_Slab::new);
        skillMap.put(Impending_Stab.NAME, Impending_Stab::new);
        skillMap.put(Galdr_Sorcery.NAME, Galdr_Sorcery::new);
        skillMap.put(Nebula.NAME, Nebula::new);
        skillMap.put(Reality_Twist.NAME, Reality_Twist::new);
        skillMap.put(Shield_Slam.NAME, Shield_Slam::new);
        skillMap.put(Totem_Strike.NAME, Totem_Strike::new);
        skillMap.put(Arcana_Drain.NAME, Arcana_Drain::new);
        skillMap.put(Glyph_Rearrangement.NAME, Glyph_Rearrangement::new);
        skillMap.put(Glyph_of_Lightning.NAME, Glyph_of_Lightning::new);
        skillMap.put(Glyph_of_Aqua.NAME, Glyph_of_Aqua::new);
        skillMap.put(Ricochet.NAME, Ricochet::new);
        skillMap.put(Forking_Bolt.NAME, Forking_Bolt::new);
        skillMap.put(Block.NAME, Block::new);
        skillMap.put(Dodge.NAME, Dodge::new);
        skillMap.put(Beyond_the_Furthest_End.NAME, Beyond_the_Furthest_End::new);
        skillMap.put(Gravitational_Field.NAME, Gravitational_Field::new);
        skillMap.put(Trip_Shot.NAME, Trip_Shot::new);
        skillMap.put(Disarm.NAME, Disarm::new);
        skillMap.put(Sixth_Sense.NAME, Sixth_Sense::new);
        skillMap.put(Catcher.NAME, Catcher::new);
        skillMap.put(Evitable.NAME, Evitable::new);
        skillMap.put(Dance.NAME, Dance::new);
        skillMap.put(Shadow_Step.NAME, Shadow_Step::new);
        skillMap.put(Step_Close.NAME, Step_Close::new);
        skillMap.put(Scratch.NAME, Scratch::new);
        skillMap.put(Rapid_Heal.NAME, Rapid_Heal::new);
        skillMap.put(Lethal_Strike.NAME, Lethal_Strike::new); //fix crit
        skillMap.put(SLAM.NAME, SLAM::new);
        skillMap.put(Step_On.NAME, Step_On::new);
        skillMap.put(STUN.NAME, STUN::new);
        skillMap.put(Deep_Wound.NAME, Deep_Wound::new);
        skillMap.put(Sword_of_Light.NAME, Sword_of_Light::new);
        skillMap.put(Greater_Healing.NAME, Greater_Healing::new);
        skillMap.put(Quick_Slashes.NAME, Quick_Slashes::new);
        skillMap.put(Paper_Rush.NAME, Paper_Rush::new);
        skillMap.put(Paper_Slash.NAME, Paper_Slash::new);
        skillMap.put(Auto_Guard.NAME, Auto_Guard::new);
        skillMap.put(Counter_Slash.NAME, Counter_Slash::new);
        skillMap.put(Back_Stab.NAME, Back_Stab::new);
        skillMap.put(Knocking_Push.NAME, Knocking_Push::new);
        skillMap.put(Leading_Light.NAME, Leading_Light::new);
        skillMap.put(Memories_Rewrite.NAME, Memories_Rewrite::new);
        skillMap.put(Amnesia.NAME, Amnesia::new);
        skillMap.put(Dread_Haunt.NAME, Dread_Haunt::new);
        skillMap.put(Disordered.NAME, Disordered::new);
        skillMap.put(Over_Flowing_Wisdom.NAME, Over_Flowing_Wisdom::new);
        skillMap.put(Blue_Seal.NAME, Blue_Seal::new);
        skillMap.put(Reverse_Sanity.NAME, Reverse_Sanity::new);
        skillMap.put(Six_Directions.NAME, Six_Directions::new);
        skillMap.put(Hands_Chains.NAME, Hands_Chains::new);
        skillMap.put(Gust.NAME, Gust::new);
        skillMap.put(Wind_Barrier.NAME, Wind_Barrier::new);
        skillMap.put(Sandevistan.NAME, Sandevistan::new);
        skillMap.put(Aruuk_Drop.NAME, Aruuk_Drop::new); //unfinished
        skillMap.put(Judgement_of_Texen.NAME, Judgement_of_Texen::new);
        skillMap.put(Starbound_Invoker.NAME, Starbound_Invoker::new);
        skillMap.put(Over_The_Rainbow.NAME, Over_The_Rainbow::new);
        skillMap.put(Paper_March.NAME, Paper_March::new);
        skillMap.put(Infernal_Regeneration_Crimson_Reindeer.NAME, Infernal_Regeneration_Crimson_Reindeer::new);
        skillMap.put(Double_Kick.NAME, Double_Kick::new);
        skillMap.put(Strike_Twice.NAME, Strike_Twice::new);
        skillMap.put(Strike_Thrice.NAME, Strike_Thrice::new);
        skillMap.put(Infernal_Imbue.NAME, Infernal_Imbue::new);
        skillMap.put(Starbound_Immortal.NAME, Starbound_Immortal::new);
        skillMap.put(Armor_Complexity.NAME, Armor_Complexity::new);
        skillMap.put(Blood_Drink.NAME, Blood_Drink::new);
        skillMap.put(Bonding.NAME, Bonding::new);
        skillMap.put(Critical_Point_Protection.NAME, Critical_Point_Protection::new);
        skillMap.put(Death_Avoidance.NAME, Death_Avoidance::new);
        skillMap.put(Devouring.NAME, Devouring::new);
        skillMap.put(Dream_Dominion.NAME, Dream_Dominion::new);
        skillMap.put(Forbidden_Rite.NAME, Forbidden_Rite::new);
        skillMap.put(Fortified.NAME, Fortified::new);
        skillMap.put(Grim_Feast.NAME, Grim_Feast::new);
        skillMap.put(Hardening.NAME, Hardening::new); //unfinished
        skillMap.put(Last_Stand.NAME, Last_Stand::new);
        skillMap.put(Light_Redirect.NAME, Light_Redirect::new);
        skillMap.put(Lumen_Distortion.NAME, Lumen_Distortion::new);
        skillMap.put(Magic_Destruction.NAME, Magic_Destruction::new);
        skillMap.put(Mana_Shield.NAME, Mana_Shield::new);
        skillMap.put(Reality_Avoidance.NAME, Reality_Avoidance::new);
        skillMap.put(Slip.NAME, Slip::new);
        skillMap.put(Spirit_Shift.NAME, Spirit_Shift::new);
        skillMap.put(Unwavering.NAME, Unwavering::new);
        skillMap.put(Vivid_Dream.NAME, Vivid_Dream::new);
        skillMap.put(Vivid_Repulsion.NAME, Vivid_Repulsion::new);
        skillMap.put(Volcanica.NAME, Volcanica::new);
        skillMap.put(Warlords_Endurance.NAME, Warlords_Endurance::new);
        skillMap.put(Aiming.NAME, Aiming::new);
        skillMap.put(Another_Slash.NAME, Another_Slash::new);
        skillMap.put(Blink_Slash.NAME, Blink_Slash::new);
        skillMap.put(Bomber.NAME, Bomber::new);
        skillMap.put(Bounded_Spells.NAME, Bounded_Spells::new);
        skillMap.put(Chill_Imbue.NAME, Chill_Imbue::new);
        skillMap.put(Cursed_Touch.NAME, Cursed_Touch::new);
        skillMap.put(Death_Sentence.NAME, Death_Sentence::new);
        skillMap.put(Deaths_Chosen.NAME, Deaths_Chosen::new);
        skillMap.put(Decimation.NAME, Decimation::new);
        skillMap.put(Defense_Ignorance.NAME, Defense_Ignorance::new); //unfinished
        skillMap.put(Double_Slash.NAME, Double_Slash::new);
        skillMap.put(Dream_Destination.NAME, Dream_Destination::new);
        skillMap.put(Dream_Repeat.NAME, Dream_Repeat::new);
        skillMap.put(Eclipse.NAME, Eclipse::new);
        skillMap.put(Elemental_Piercing.NAME, Elemental_Piercing::new);
        skillMap.put(Eternal_Dream.NAME, Eternal_Dream::new);
        skillMap.put(Even_In_Death.NAME, Even_In_Death::new); //can improve
        skillMap.put(Explosion.NAME, Explosion::new);
        skillMap.put(Fang_Stab.NAME, Fang_Stab::new);
        skillMap.put(Far_Reach.NAME, Far_Reach::new);
        skillMap.put(Final_Judgement.NAME, Final_Judgement::new);
        skillMap.put(Firing_Focus.NAME, Firing_Focus::new);
        skillMap.put(Firm_Grip.NAME, Firm_Grip::new);
        skillMap.put(Flame_Explode.NAME, Flame_Explode::new);
        skillMap.put(Flame_Imbue.NAME, Flame_Imbue::new); //unfinished
        skillMap.put(Flame_Infusion.NAME, Flame_Infusion::new);
        skillMap.put(Frenzy_Destruction.NAME, Frenzy_Destruction::new);
        skillMap.put(Furious_Destruction.NAME, Furious_Destruction::new); //can improve
        skillMap.put(Heavy_Piercing_Rounds.NAME, Heavy_Piercing_Rounds::new);
        skillMap.put(Hell_Inferno.NAME, Hell_Inferno::new);
        skillMap.put(Hundred_Wounds.NAME, Hundred_Wounds::new);
        skillMap.put(Implosion.NAME, Implosion::new);
        skillMap.put(Kineticist.NAME, Kineticist::new);
        skillMap.put(Pain_Whisperer.NAME, Pain_Whisperer::new);
        skillMap.put(Piercing_Cold.NAME, Piercing_Cold::new);
        skillMap.put(Piercing_Destruction.NAME, Piercing_Destruction::new); //can improve
        skillMap.put(Pistol_Duel.NAME, Pistol_Duel::new);
        skillMap.put(Precise_Precision.NAME, Precise_Precision::new);
        skillMap.put(Pulverize.NAME, Pulverize::new);
        skillMap.put(Pure_Destruction.NAME, Pure_Destruction::new); //can improve
        skillMap.put(Python_Bite.NAME, Python_Bite::new); //can improve
        skillMap.put(Quick_Reload.NAME, Quick_Reload::new);
        skillMap.put(Reaping.NAME, Reaping::new); //can improve
        skillMap.put(Reflecting_Destruction.NAME, Reflecting_Destruction::new);
        skillMap.put(Revelation.NAME, Revelation::new);
        skillMap.put(Ricochet_Shot.NAME, Ricochet_Shot::new);
        skillMap.put(Ricochet_Slash.NAME, Ricochet_Slash::new);
        skillMap.put(Royal_Retribute.NAME, Royal_Retribute::new);
        skillMap.put(Seven_Shade_Destruction.NAME, Seven_Shade_Destruction::new);
        skillMap.put(Shadestriker.NAME, Shadestriker::new);
        skillMap.put(Shepherds_Technique.NAME, Shepherds_Technique::new);
        skillMap.put(Short_Recurve.NAME, Short_Recurve::new);
        skillMap.put(Soul_Extraction.NAME, Soul_Extraction::new);
        skillMap.put(Spell_Spectrum.NAME, Spell_Spectrum::new);
        skillMap.put(Splitting_Steel.NAME, Splitting_Steel::new); //unfinished
        skillMap.put(Switching.NAME, Switching::new);
        skillMap.put(Tearing.NAME, Tearing::new); //can improve
        skillMap.put(The_Ruin.NAME, The_Ruin::new);
        skillMap.put(Third_Impact.NAME, Third_Impact::new); //can improve
        skillMap.put(Thorns.NAME, Thorns::new); //can improve
        skillMap.put(Thunder_Call.NAME, Thunder_Call::new);
        skillMap.put(Tide_Destruction.NAME, Tide_Destruction::new);
        skillMap.put(Tolerated_Destruction.NAME, Tolerated_Destruction::new); //unfinished
        skillMap.put(Torment.NAME, Torment::new);
        skillMap.put(Unreaching.NAME, Unreaching::new);
        skillMap.put(Unrelenting_Destruction.NAME, Unrelenting_Destruction::new);
        skillMap.put(Unstable_Blood.NAME, Unstable_Blood::new); //unfinished
        skillMap.put(Vengeful_Destruction.NAME, Vengeful_Destruction::new);
        skillMap.put(Warfare_Devastation.NAME, Warfare_Devastation::new);
        skillMap.put(Water_Infusion.NAME, Water_Infusion::new);
        skillMap.put(Absolute_Order.NAME, Absolute_Order::new); //unfinished
        skillMap.put(Ascendants_Grace.NAME, Ascendants_Grace::new);
        skillMap.put(Chain_Restoration.NAME, Chain_Restoration::new); //unfinished
        skillMap.put(Clearance.NAME, Clearance::new);
        skillMap.put(Dawn_Caller.NAME, Dawn_Caller::new); //unfinished
        skillMap.put(Emperors_Bound.NAME, Emperors_Bound::new); //unfinished
        skillMap.put(Emperors_Command.NAME, Emperors_Command::new);
        skillMap.put(Fleeting_Guidance.NAME, Fleeting_Guidance::new);
        skillMap.put(From_Legion.NAME, From_Legion::new);
        skillMap.put(Grip_From_The_Dark.NAME, Grip_From_The_Dark::new);
        skillMap.put(Grow.NAME, Grow::new);
        skillMap.put(Spark_Guidance.NAME, Spark_Guidance::new);
        skillMap.put(Locating.NAME, Locating::new);
        skillMap.put(Misperception.NAME, Misperception::new);
        skillMap.put(Mitigating_Destruction.NAME, Mitigating_Destruction::new);
        skillMap.put(Nightingales_Rhythm.NAME, Nightingales_Rhythm::new);
        skillMap.put(Pain_Shift.NAME, Pain_Shift::new); //unfinished
        skillMap.put(Precise_Vision.NAME, Precise_Vision::new);
        skillMap.put(Soul_Guidance.NAME, Soul_Guidance::new);
        skillMap.put(Soul_Harvest.NAME, Soul_Harvest::new);
        skillMap.put(Spectrum_Restoration.NAME, Spectrum_Restoration::new);
        skillMap.put(Spell_Infusion.NAME, Spell_Infusion::new);
        skillMap.put(Sudden_Fortification.NAME, Sudden_Fortification::new);
        skillMap.put(Alternative_Pain.NAME, Alternative_Pain::new);
        skillMap.put(Anchor_Point.NAME, Anchor_Point::new);
        skillMap.put(Arcane_Infused.NAME, Arcane_Infused::new);
        skillMap.put(Arcanoxs_Command.NAME, Arcanoxs_Command::new);
        skillMap.put(Ascended_Hands.NAME, Ascended_Hands::new);
        skillMap.put(Blinding_Light.NAME, Blinding_Light::new); //can improve
        skillMap.put(Blinking_Destruction.NAME, Blinking_Destruction::new);
        skillMap.put(Blunt_Claws.NAME, Blunt_Claws::new);
        skillMap.put(Concealing_Shadow.NAME, Concealing_Shadow::new);
        skillMap.put(Creators_Hands.NAME, Creators_Hands::new);
        skillMap.put(Cripple_Execution.NAME, Cripple_Execution::new);
        skillMap.put(Curse_of_Tissart.NAME, Curse_of_Tissart::new); //unfinished
        skillMap.put(Death_Shroud.NAME, Death_Shroud::new);
        skillMap.put(Depth_Seal.NAME, Depth_Seal::new); //unfinished
        skillMap.put(Dominating_Treads.NAME, Dominating_Treads::new); //can improve
        skillMap.put(Dream_Conquer.NAME, Dream_Conquer::new); //can improve
        skillMap.put(Earth_Spike.NAME, Earth_Spike::new);
        skillMap.put(Elysian_Touch.NAME, Elysian_Touch::new);
        skillMap.put(Equinox.NAME, Equinox::new);
        skillMap.put(Eterweight.NAME, Eterweight::new);
        skillMap.put(Ethereal_Shift.NAME, Ethereal_Shift::new);
        skillMap.put(Extra_Step.NAME, Extra_Step::new);
        skillMap.put(Feet_Hex.NAME, Feet_Hex::new);
        skillMap.put(Flash.NAME, Flash::new);
        skillMap.put(Frost_Chain.NAME, Frost_Chain::new);
        skillMap.put(Frozen_Wound.NAME, Frozen_Wound::new);
        skillMap.put(Hard_Hitting.NAME, Hard_Hitting::new); //unfinished
        skillMap.put(Hunts_Ender.NAME, Hunts_Ender::new);
        skillMap.put(In_Depth_Destruction.NAME, In_Depth_Destruction::new);
        skillMap.put(Inaccurate.NAME, Inaccurate::new);
        skillMap.put(Insanity.NAME, Insanity::new);
        skillMap.put(Knights_Pride.NAME, Knights_Pride::new);
        skillMap.put(Last_Resort.NAME, Last_Resort::new); //unfinished
        skillMap.put(Last_Wish.NAME, Last_Wish::new);
        skillMap.put(Lightdance.NAME, Lightdance::new);
        skillMap.put(Limits_End.NAME, Limits_End::new);
        skillMap.put(Lingering_Force.NAME, Lingering_Force::new);
        skillMap.put(Linking_Chains.NAME, Linking_Chains::new); //unfinished
        skillMap.put(Magic_Deflection.NAME, Magic_Deflection::new);
        skillMap.put(Midnight_Reflection.NAME, Midnight_Reflection::new);
        skillMap.put(Moon_Infusion.NAME, Moon_Infusion::new);
        skillMap.put(Moondance.NAME, Moondance::new);
        skillMap.put(Open_Wound.NAME, Open_Wound::new); //unfinished
        skillMap.put(Paranoid_Veil.NAME, Paranoid_Veil::new);
        skillMap.put(Pressure_Wound.NAME, Pressure_Wound::new); //unfinished
        skillMap.put(Quick_Hands.NAME, Quick_Hands::new);
        skillMap.put(Quick_Resurrection.NAME, Quick_Resurrection::new);
        skillMap.put(Quick_Slash.NAME, Quick_Slash::new);
        skillMap.put(Restriction.NAME, Restriction::new); //unfinished
        skillMap.put(Selfish_Influence.NAME, Selfish_Influence::new);
        skillMap.put(Seven_Step_Destruction.NAME, Seven_Step_Destruction::new);
        skillMap.put(Slamming.NAME, Slamming::new);
        skillMap.put(Soul_Drain.NAME, Soul_Drain::new);
        skillMap.put(Sound_Amplifier.NAME, Sound_Amplifier::new);
        skillMap.put(Spectral_Step.NAME, Spectral_Step::new);
        skillMap.put(Spell_Carve.NAME, Spell_Carve::new);
        skillMap.put(Spell_Deconstruct.NAME, Spell_Deconstruct::new);
        skillMap.put(Spell_Overflow.NAME, Spell_Overflow::new);
        skillMap.put(Spirit_Enclose.NAME, Spirit_Enclose::new);
        skillMap.put(Spirit_Urn.NAME, Spirit_Urn::new);
        skillMap.put(Storm_Destruction.NAME, Storm_Destruction::new);
        skillMap.put(SWitch_Craft.NAME, SWitch_Craft::new);
        skillMap.put(Target_Focus.NAME, Target_Focus::new);
        skillMap.put(Termination.NAME, Termination::new);
        skillMap.put(The_Forgotten.NAME, The_Forgotten::new);
        skillMap.put(Time_Returns.NAME, Time_Returns::new);
        skillMap.put(Too_Heavy.NAME, Too_Heavy::new);
        skillMap.put(Trump_Card.NAME, Trump_Card::new); //unfinished
        skillMap.put(Unflow.NAME, Unflow::new);
        skillMap.put(Unpredictable_Pain.NAME, Unpredictable_Pain::new);
        skillMap.put(Unreal_Illusion.NAME, Unreal_Illusion::new);
        skillMap.put(Verifyr_Passion.NAME, Verifyr_Passion::new); //unfinished
        skillMap.put(Vision_Loss.NAME, Vision_Loss::new);
        skillMap.put(Vitharas_Technique.NAME, Vitharas_Technique::new);
        skillMap.put(Vivid_Pain.NAME, Vivid_Pain::new);
        skillMap.put(Wanderer.NAME, Wanderer::new);
        skillMap.put(Webbers_Play.NAME, Webbers_Play::new); //unfinished
        skillMap.put(Woodsnare.NAME, Woodsnare::new); //unfinished
        skillMap.put(Controlled_Destruction.NAME, Controlled_Destruction::new);
        skillMap.put(Untouchable.NAME, Untouchable::new); //unfinished
        skillMap.put(Oslanias_Blessing.NAME, Oslanias_Blessing::new); //unfinished
        skillMap.put(Moon_Pray.NAME, Moon_Pray::new);
        skillMap.put(Moon_Beam.NAME, Moon_Beam::new);
        skillMap.put(Moon_Blast.NAME, Moon_Blast::new);
        skillMap.put(Titanic.NAME, Titanic::new);
        skillMap.put(Titanium.NAME, Titanium::new);
        skillMap.put(Spark_Force.NAME, Spark_Force::new);
        skillMap.put(sixteenth_Inner_Light.NAME, sixteenth_Inner_Light::new);
        skillMap.put(Hydro_Beam.NAME, Hydro_Beam::new);
        skillMap.put(High_Tides.NAME, High_Tides::new);
        skillMap.put(Rain_Call.NAME, Rain_Call::new);
        skillMap.put(Twin_Fang.NAME, Twin_Fang::new);
        skillMap.put(Tail_Whip.NAME, Tail_Whip::new);
        skillMap.put(Condensed_Hydro_Beam.NAME, Condensed_Hydro_Beam::new);
        skillMap.put(Death_Ray.NAME, Death_Ray::new);
        skillMap.put(Deepblue_Cataclysm.NAME, Deepblue_Cataclysm::new);
        skillMap.put(Swift_Hydro_Beam.NAME, Swift_Hydro_Beam::new);
        skillMap.put(Hydro_Burst.NAME, Hydro_Burst::new);
        skillMap.put(Refreshing.NAME, Refreshing::new);
        skillMap.put(Tail_Pierce.NAME, Tail_Pierce::new);
        skillMap.put(Sun_Prism.NAME, Sun_Prism::new);
        skillMap.put(Time_Fracture.NAME, Time_Fracture::new);
        skillMap.put(Pinning_Shot.NAME, Pinning_Shot::new);
        skillMap.put(Identity_Of_Light.NAME, Identity_Of_Light::new); //unfinished
        skillMap.put(Seal_Of_Light.NAME, Seal_Of_Light::new);
        skillMap.put(Piercing_Light.NAME, Piercing_Light::new);
        skillMap.put(Refracting_Light.NAME, Refracting_Light::new); //can improve
        skillMap.put(Uncontained_Light.NAME, Uncontained_Light::new);
        skillMap.put(Duple_Light.NAME, Duple_Light::new); //unfinished
        skillMap.put(Fallen_Light.NAME, Fallen_Light::new);
        skillMap.put(Veil_of_Light.NAME, Veil_of_Light::new);
        skillMap.put(Twisted_Mind.NAME, Twisted_Mind::new); //unfinished
        skillMap.put(Phantoms_Fortress.NAME, Phantoms_Fortress::new);
        skillMap.put(Infernal_Flame.NAME, Infernal_Flame::new);
        skillMap.put(Absolute_Zero.NAME, Absolute_Zero::new);
        skillMap.put(Snow_Fall.NAME, Snow_Fall::new);
        skillMap.put(Comet.NAME, Comet::new);
        skillMap.put(Snowy_Steps.NAME, Snowy_Steps::new);
        skillMap.put(Deepest_Aqua.NAME, Deepest_Aqua::new);
        skillMap.put(Spinning.NAME, Spinning::new);
        skillMap.put(Rend_Cross.NAME, Rend_Cross::new); //can improve
        skillMap.put(Final_Edge.NAME, Final_Edge::new);
        skillMap.put(Knocking.NAME, Knocking::new);
        skillMap.put(Switching_Step.NAME, Switching_Step::new);
        skillMap.put(Flame_Call.NAME, Flame_Call::new);
        skillMap.put(Fire_Pillar.NAME, Fire_Pillar::new);
        skillMap.put(Time_Bank.NAME, Time_Bank::new);
        skillMap.put(Seal_Of_Hope.NAME, Seal_Of_Hope::new);
        skillMap.put(Hope_And_Insanity.NAME, Hope_And_Insanity::new);
        skillMap.put(Blade_Steps.NAME, Blade_Steps::new);
        skillMap.put(Golden_Seal.NAME, Golden_Seal::new);
        skillMap.put(Mist_Form.NAME, Mist_Form::new);
        skillMap.put(Echo_In_Time.NAME, Echo_In_Time::new); //unfinished
        skillMap.put(Light_Step.NAME, Light_Step::new);
        skillMap.put(Fallen_Blessing.NAME, Fallen_Blessing::new);
        skillMap.put(Light_Being.NAME, Light_Being::new); //unfinished
        skillMap.put(Magic_Refraction.NAME, Magic_Refraction::new);
        skillMap.put(Controlled_Curve.NAME, Controlled_Curve::new);
        skillMap.put(Designation.NAME, Designation::new);
        skillMap.put(Conduct.NAME, Conduct::new);
        skillMap.put(Warp_Chain.NAME, Warp_Chain::new);
        skillMap.put(Refresh.NAME, Refresh::new);
        skillMap.put(Backtrack.NAME, Backtrack::new); //unfinished
        skillMap.put(Clock_Hand.NAME, Clock_Hand::new);
        skillMap.put(Magic_Divinity.NAME, Magic_Divinity::new);
        skillMap.put(Omnistrength.NAME, Omnistrength::new);
        skillMap.put(No_Weighted_Arms.NAME, No_Weighted_Arms::new);
        skillMap.put(Light_Chamber.NAME, Light_Chamber::new); //can improve
        skillMap.put(Light_Fracture.NAME, Light_Fracture::new);
        skillMap.put(Third_Resonance.NAME, Third_Resonance::new); //can improve
        skillMap.put(Aquatic_Sense.NAME, Aquatic_Sense::new);
        skillMap.put(Quicker_Moves.NAME, Quicker_Moves::new);
        skillMap.put(From_Side_From_Behind.NAME, From_Side_From_Behind::new);
        skillMap.put(Collision_Tear.NAME, Collision_Tear::new);
        skillMap.put(Luck_Reversal.NAME, Luck_Reversal::new);
        skillMap.put(Primal_Instinct.NAME, Primal_Instinct::new);
        skillMap.put(Sea_Step.NAME, Sea_Step::new);
        skillMap.put(Steady_Hands.NAME, Steady_Hands::new);
        skillMap.put(Reactive_Blood.NAME, Reactive_Blood::new);
        skillMap.put(Immortalis.NAME, Immortalis::new);
        skillMap.put(Towering.NAME, Towering::new);
        skillMap.put(Massive_Protection.NAME, Massive_Protection::new);
        skillMap.put(Physical_Mastery.NAME, Physical_Mastery::new);
        skillMap.put(Weapon_Blocking.NAME, Weapon_Blocking::new);
        skillMap.put(Rainborn.NAME, Rainborn::new);
        skillMap.put(Unusual_Evolution.NAME, Unusual_Evolution::new);
        skillMap.put(With_The_Flow.NAME, With_The_Flow::new);
        skillMap.put(Grip_Of_Vengeance.NAME, Grip_Of_Vengeance::new);
        skillMap.put(Hope_Infusion.NAME, Hope_Infusion::new);
        skillMap.put(Innate_Stab.NAME, Innate_Stab::new);
        skillMap.put(Forged_In_Hate.NAME, Forged_In_Hate::new);
        skillMap.put(Do_And_Die.NAME, Do_And_Die::new);
        skillMap.put(Blade_Call.NAME, Blade_Call::new); //can improve
        skillMap.put(Pain_Augment.NAME, Pain_Augment::new);
        skillMap.put(Indirect_Release.NAME, Indirect_Release::new);
        skillMap.put(Commit_Strike.NAME, Commit_Strike::new);
        skillMap.put(Thermal_Explosion.NAME, Thermal_Explosion::new);
        skillMap.put(Uneasy_Handling.NAME, Uneasy_Handling::new);
        skillMap.put(Long_Edge.NAME, Long_Edge::new);
        skillMap.put(Vitality_Overload.NAME, Vitality_Overload::new);
        skillMap.put(Eye_To_Eye.NAME, Eye_To_Eye::new);
        skillMap.put(Her_Grace.NAME, Her_Grace::new);
        skillMap.put(Pain_Amplify.NAME, Pain_Amplify::new);
        skillMap.put(Seidr_Sorcery_Verry_And_Tori.NAME, Seidr_Sorcery_Verry_And_Tori::new);
        skillMap.put(Mythic_Flow.NAME, Mythic_Flow::new);
        skillMap.put(Mythic_Revelation.NAME, Mythic_Revelation::new);
        skillMap.put(Boundwave.NAME, Boundwave::new);
        skillMap.put(Chosen_Endpoint.NAME, Chosen_Endpoint::new); //can improve
        skillMap.put(False_Authority.NAME, False_Authority::new);
        skillMap.put(Hemonieas_Vessel.NAME, Hemonieas_Vessel::new);
        skillMap.put(Stars_Unseal.NAME, Stars_Unseal::new); //unfinished
        skillMap.put(Skys_Judgement.NAME, Skys_Judgement::new); //unfinished
        skillMap.put(Adrenaline.NAME, Adrenaline::new); //can improve
        skillMap.put(Kill_Command.NAME, Kill_Command::new);
        skillMap.put(Borrowed_Time.NAME, Borrowed_Time::new);
        skillMap.put(Manipulation.NAME, Manipulation::new);
        skillMap.put(Life_Transfer.NAME, Life_Transfer::new); //unfinished
        skillMap.put(Blessed.NAME, Blessed::new);
        skillMap.put(Telepathy.NAME, Telepathy::new);
        skillMap.put(The_Sacrificed.NAME, The_Sacrificed::new);
        skillMap.put(Endless_Critical.NAME, Endless_Critical::new);
        skillMap.put(Tether.NAME, Tether::new);
        skillMap.put(Rain_of_Stars.NAME, Rain_of_Stars::new);
        skillMap.put(Reverse_Polarity.NAME, Reverse_Polarity::new);
        skillMap.put(Luminous.NAME, Luminous::new);
        skillMap.put(Silent_Steps.NAME, Silent_Steps::new);
        skillMap.put(Perfect_Intervention.NAME, Perfect_Intervention::new);
        skillMap.put(Ark_Of_The_Lost_Souls.NAME, Ark_Of_The_Lost_Souls::new);
        skillMap.put(Pride_Of_The_Blood_Pact.NAME, Pride_Of_The_Blood_Pact::new);
        skillMap.put(Thrive_Against_The_Despair.NAME, Thrive_Against_The_Despair::new);
        skillMap.put(Guidance_Towards_The_End.NAME, Guidance_Towards_The_End::new);
        skillMap.put(Leda_Of_Canopus.NAME, Leda_Of_Canopus::new);
        skillMap.put(Unravel.NAME, Unravel::new);
        skillMap.put(Vow_Of_Iron.NAME, Vow_Of_Iron::new);
        skillMap.put(Undone.NAME, Undone::new);
        skillMap.put(Scales_Of_Dragon.NAME, Scales_Of_Dragon::new);
        skillMap.put(Crux_Mortis.NAME, Crux_Mortis::new);
        skillMap.put(Starving_Beast.NAME, Starving_Beast::new);
        skillMap.put(Gaze_From_The_False_Moon.NAME, Gaze_From_The_False_Moon::new);
        skillMap.put(For_Everyone.NAME, For_Everyone::new);
        skillMap.put(Die_Slowly.NAME, Die_Slowly::new);
        skillMap.put(The_Demon_Is_Me.NAME, The_Demon_Is_Me::new); //add 'when poison, bleed, ignite' event
        skillMap.put(Exile.NAME, Exile::new);
        skillMap.put(True_Essence_Regalia.NAME, True_Essence_Regalia::new);
        skillMap.put(Pounce_On_The_Prey.NAME, Pounce_On_The_Prey::new);
        skillMap.put(Forbidden_Voidshades_Link.NAME, Forbidden_Voidshades_Link::new);
        skillMap.put(Ode_Of_The_Shooting_Star.NAME, Ode_Of_The_Shooting_Star::new); //add fire / light damage choice
        skillMap.put(Mythic_Invocation.NAME, Mythic_Invocation::new);
        skillMap.put(Enders_Calling.NAME, Enders_Calling::new);
        skillMap.put(Time_Overridden.NAME, Time_Overridden::new);
        skillMap.put(Condensed_Sun_Ray.NAME, Condensed_Sun_Ray::new);
        skillMap.put(Weak_Seeking_System.NAME, Weak_Seeking_System::new);
        skillMap.put(Paper_Storm.NAME, Paper_Storm::new);
        skillMap.put(Mana_Arrow.NAME, Mana_Arrow::new);
        skillMap.put(Gathering_Storm.NAME, Gathering_Storm::new);
        skillMap.put(Burst_Nova.NAME, Burst_Nova::new);
        skillMap.put(Solar_Shield.NAME, Solar_Shield::new);
        skillMap.put(Poison_Spit.NAME, Poison_Spit::new);
        skillMap.put(Condensed_Striking_Light.NAME, Condensed_Striking_Light::new);
        skillMap.put(Quick_Charge.NAME, Quick_Charge::new); //can improve
        skillMap.put(Phantom_Strike.NAME, Phantom_Strike::new);
        skillMap.put(Aleph_Infinity_Invocation.NAME, Aleph_Infinity_Invocation::new);
        skillMap.put(Omega_Function_Invocation.NAME, Omega_Function_Invocation::new);
        skillMap.put(XYZ_Invocation.NAME, XYZ_Invocation::new);
        skillMap.put(Bandage_Fix.NAME, Bandage_Fix::new);
        skillMap.put(Inside_Cut.NAME, Inside_Cut::new);
        skillMap.put(Stance_Brace.NAME, Stance_Brace::new); //can improve
        skillMap.put(Drawing_Vortex.NAME, Drawing_Vortex::new);
        skillMap.put(Magic_Reflection.NAME, Magic_Reflection::new);
        skillMap.put(Stomp.NAME, Stomp::new);
        skillMap.put(Spectral_Feather.NAME, Spectral_Feather::new);
        skillMap.put(Stampede.NAME, Stampede::new);
        skillMap.put(Cross_Slash.NAME, Cross_Slash::new);
        skillMap.put(Sun_Bless.NAME, Sun_Bless::new);
        skillMap.put(Sun_Beam.NAME, Sun_Beam::new);
        skillMap.put(Sun_Blast.NAME, Sun_Blast::new);
        skillMap.put(Chimeric_Adaptation.NAME, Chimeric_Adaptation::new);
        skillMap.put(Fury_Swipe.NAME, Fury_Swipe::new);
        skillMap.put(Rage.NAME, Rage::new);
        skillMap.put(Petrifying_Gaze.NAME, Petrifying_Gaze::new);
        skillMap.put(Front_Kick.NAME, Front_Kick::new);
        skillMap.put(Ground_Shatter.NAME, Ground_Shatter::new);
        skillMap.put(Aeralith_Huntpath.NAME, Aeralith_Huntpath::new);
        skillMap.put(Destiny_Born.NAME, Destiny_Born::new);
        skillMap.put(Second_Destiny.NAME, Second_Destiny::new);
        skillMap.put(Thread_Of_Chance.NAME, Thread_Of_Chance::new);
        skillMap.put(Thread_Of_Fate.NAME, Thread_Of_Fate::new);
        skillMap.put(High_Speed_Of_Divine_Word.NAME, High_Speed_Of_Divine_Word::new);
        skillMap.put(After_Image.NAME, After_Image::new);
        skillMap.put(After_Steps.NAME, After_Steps::new);
        skillMap.put(Extreme_Possibility.NAME, Extreme_Possibility::new);
        skillMap.put(Vulnerable_Moments.NAME, Vulnerable_Moments::new);
        skillMap.put(Directed_Timeline.NAME, Directed_Timeline::new);
        skillMap.put(Draining_Strike.NAME, Draining_Strike::new);
        skillMap.put(Arcane_Shot.NAME, Arcane_Shot::new);
        skillMap.put(Heal_Chain.NAME, Heal_Chain::new);
        skillMap.put(Auto_Block.NAME, Auto_Block::new);
        skillMap.put(Body_Switch.NAME, Body_Switch::new);
        skillMap.put(Fan_The_Flames.NAME, Fan_The_Flames::new);
        skillMap.put(Death_Pressure.NAME, Death_Pressure::new);
        skillMap.put(Sun_Reflect.NAME, Sun_Reflect::new);
        skillMap.put(Elandaria.NAME, Elandaria::new);
        skillMap.put(Dawn_Bringer.NAME, Dawn_Bringer::new);
        skillMap.put(Divinarius.NAME, Divinarius::new);
        skillMap.put(Istenta.NAME, Istenta::new);
        skillNames = new ArrayList<>(skillMap.keySet());
    }

    public static Skill getSkill(String name, Unit unit, boolean isActive) {
        Supplier<Skill> skillFactory = skillMap.get(name);
        if (skillFactory != null) {
            Skill skill = skillFactory.get();
            skill.setUser(unit);
            skill.setIsActive(isActive);
            return skill;
        }
        return null;
    }

    public static Skill getSkill(String name) {
        Supplier<Skill> skillFactory = skillMap.get(name);
        if (skillFactory != null) {
            Skill skill = skillFactory.get();
            skill.setIsActive(true);
            return skill;
        }
        return null;  // ถ้าไม่มี skill ตามชื่อ
    }

}
