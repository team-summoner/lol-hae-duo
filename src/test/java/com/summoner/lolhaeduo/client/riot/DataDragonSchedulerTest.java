package com.summoner.lolhaeduo.client.riot;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DataDragonSchedulerTest {

//    @Autowired
//    private DataDragonScheduler dataDragonScheduler;
//
//    @Autowired
//    private VersionRepository versionRepository;
//
//    @Autowired
//    private ChampionRepository championRepository;
//
//    @Autowired
//    private ProfileIconRepository profileIconRepository;
//
//    @BeforeEach
//    void setUp() {
//        // starting from empty DB
//        versionRepository.deleteAll();
//        championRepository.deleteAll();
//        profileIconRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("verify if the taken data is not null")
//    void test1() {
//        // execute method
//        dataDragonScheduler.updateDataDragonInfo();
//
//        // verify data : Version
//        List<Version> versions = versionRepository.findAll();
//        assertThat(versions).hasSize(1);
//        String latestVersion = versions.get(0).getVersionNumber();
//        assertThat(latestVersion).isNotBlank();
//
//        // verify data : Champion
//        List<Champion> champions = championRepository.findAll();
//        assertThat(champions).isNotEmpty();
//        Optional<Champion> exampleChampion = champions.stream().findFirst();
//        assertThat(exampleChampion).isPresent();
//        assertThat(exampleChampion.get().getId()).isNotBlank();
//        assertThat(exampleChampion.get().getName()).isNotBlank();
//
//        // verify data : ProfileIcon
//        List<ProfileIcon> icons = profileIconRepository.findAll();
//        assertThat(icons).isNotEmpty();
//        Optional<ProfileIcon> exampleIcon = icons.stream().findFirst();
//        assertThat(exampleIcon).isPresent();
//        assertThat(exampleIcon.get().getId()).isNotNull();
//    }
}